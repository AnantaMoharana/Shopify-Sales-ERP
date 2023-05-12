import Jobs.SparkStreamingJob
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import Schemas.Schemas.itemSchema
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.ArrayType

//This is the ShopifyOrderETL class which is a result of extending the SparkStreamingJob abstract class
class ShopifyOrderETL(appName: String, master: String) extends SparkStreamingJob(appName,master){

  //Override the extract method and implement it to read data from our Kafka cluster
  override def extract(topicName: String, kafkaIp: String): DataFrame = {

    //Build the spark session
    val spark = SparkSession.builder()
      .appName(appName)
      .master(master)
      .config("spark.cassandra.connection.host", "127.0.0.1")
      .getOrCreate()
    //Read the data via a Spark stream and store it in a Spark dataframe
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"${kafkaIp}:9092")
      .option("subscribe", s"${topicName}")
      .load()

    return df

  }

  override def runETL(df: DataFrame): Unit = {

    //Get the orders from the Spark dataframe and store its "order_id", "date", "name", and, "total_price"
    val orders=getOrders(df)
    .selectExpr("order_id", "date","name", "total_price")

    //Get the line iten from the Spark dataframe that is being streamed
    //This contains each item in the customers order
    val lineItems=getLineItems(df)


        //Write the line items to the Cassandra data warehouse using a writeStream
        //This write stream uses batchDF to write each of the dataframes to the Cassandra data warehouse
        val orderStream=orders.writeStream
          .foreachBatch((batchDf: DataFrame, id: Long) =>{
            batchDf.write
              .format("org.apache.spark.sql.cassandra")
              .mode(SaveMode.Append) // Specify the save mode
              .option("keyspace", "shopify_erp") // Specify the keyspace
              .option("table", "orders")
              .option("write.consistency.level", "ONE")
              .save()
          })
          .trigger(Trigger.ProcessingTime("5 seconds"))
          .start()


    //Write the line items to the Cassandra data warehouse using a writeStream
    //This write stream uses batchDF to write each of the dataframes to the Cassandra data warehouse
    val itemsStream=lineItems.writeStream
      .foreachBatch((batchDf: DataFrame, id: Long) => {
        batchDf.write
          .format("org.apache.spark.sql.cassandra")
          .mode(SaveMode.Append) // Specify the save mode
          .option("keyspace", "shopify_erp") // Specify the keyspace
          .option("table", "items")
          .option("write.consistency.level", "ONE")
          .save()
        println(id)

      })
      .trigger(Trigger.ProcessingTime("5 seconds"))
      .start()


  //Await both write streams to be terminated
    orderStream.awaitTermination()
    itemsStream.awaitTermination()




  }




  //Get the orders from the Spark dataframe
  //The order being read in from a Kafka is a JSON
  //This means that when Spark reads it, it will be read in as a dataframe with a single JSON column
  //The following code formats that data and retrieves order data
  def getOrders(dataFrame: DataFrame): DataFrame = {

    //Cast the JSON to a string and then use get_json_object to get the needed fields
    return dataFrame.selectExpr("CAST(value AS STRING)")
      .withColumn("id", get_json_object(col("value"), "$.id"))
      .withColumn("current_subtotal_price", get_json_object(col("value"), "$.current_subtotal_price"))
      .withColumn("date", get_json_object(col("value"), "$.created_at"))
      .withColumn("name", get_json_object(col("value"), "$.name"))
      .withColumnRenamed("id", "order_id")
      .withColumnRenamed("current_subtotal_price", "total_price")
      .selectExpr("order_id", "date","name", "total_price")


  }

  //Get the orders from the Spark dataframe
  //The order being read in from a Kafka is a JSON
  //This means that when Spark reads it, it will be read in as a dataframe with a single JSON column
  //The following code formats that data and and retrieves data on each item in the item
  def getLineItems(dataFrame: DataFrame): DataFrame = {

  //Since this is the original unaltered dataframe from the stream, we need to cast the value as a string
  //get_json_objects is then used to get the general info about the order and the line items
    val itemOrders= dataFrame.selectExpr("CAST(value AS STRING)")
      .withColumn("id", get_json_object(col("value"), "$.id"))
      .withColumn("line_items", get_json_object(col("value"), "$.line_items"))// since line items are stored in a JSON array we need to get the arrya
      .withColumn("item_df", from_json(col("line_items"), ArrayType(itemSchema)))//We then match a schema to the lists JSONS and each json as a row
      .select(explode(col("item_df")))
      .withColumnRenamed("col", "items_sold") //We then get the needed info for each item in the order
      .selectExpr("items_sold.id",
        "items_sold.fulfillable_quantity",
        "items_sold.fulfillment_service",
        "items_sold.fulfillment_status",
        "items_sold.gift_card",
        "items_sold.grams",
        "items_sold.name",
        "items_sold.price",
        "items_sold.product_exists",
        "items_sold.product_id",
        "items_sold.quantity",
        "items_sold.requires_shipping",
        "items_sold.sku",
        "items_sold.taxable",
        "items_sold.title",
        "items_sold.total_discount",
        "items_sold.variant_id",
        "items_sold.variant_inventory_management",
        "items_sold.variant_title",
        "items_sold.vendor",
        "items_sold.tax_lines",
        "items_sold.discount_allocations")
      .withColumnRenamed("id", "item_id")

    //Taxes and discounts are stores in their own JSON array and we cant use "explode" to break the data frame up anymore
    //Because of this we need to pass the dataframe with each item and then extract the needed information

    //Get the tax data
    val lineTax = getLineTax(itemOrders)
    //Get the discount data
    val lineDisc = getLineDisc(itemOrders)
    //Join the tax and discount dataframes on the common item_id column
    val jointTaxAndDiscount = lineTax.join(lineDisc, "item_id")
    //Join the joint tax and discount dataframe with the overall items dataframe
    val itemsWithTaxAndDiscount=itemOrders.join(jointTaxAndDiscount,"item_id").drop("tax_lines","discount_allocations")

    return itemsWithTaxAndDiscount

  }
  //Get the discount information
  def getLineTax(dataFrame: DataFrame): DataFrame = {
    //Use explode the separate the tax_lines columns into individual rows and get the needed data
    return dataFrame.select(col("item_id"), explode_outer(col("tax_lines")))
      .withColumnRenamed("col", "taxes")
      .selectExpr("item_id", "taxes.title", "taxes.price", "taxes.rate")
      .withColumnRenamed("title", "tax_name")
      .withColumnRenamed("price", "tax_amount")
      .withColumnRenamed("rate", "tax_rate")

  }
  //Get the discount information
  def getLineDisc(dataFrame: DataFrame): DataFrame = {
    //Use explode the separate the discount_allocations columns into individual rows and get the needed data
    return dataFrame.select(col("item_id"), explode_outer(col("discount_allocations")))
      .withColumnRenamed("col", "discounts")
      .selectExpr("item_id", "discounts.amount")
      .withColumnRenamed("amount", "discount_amount")


  }

}
