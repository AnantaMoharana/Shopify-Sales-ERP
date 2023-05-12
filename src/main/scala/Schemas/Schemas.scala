package Schemas
import org.apache.spark.sql.types._
//Create an object to hold any necessary schema plans we may need in the transformation process
object Schemas {
//Create a schema for each item in the order
  val itemSchema = StructType(Seq(
    StructField("id", LongType, nullable = false),
    StructField("admin_graphql_api_id", StringType, nullable = false),
    StructField("fulfillable_quantity", IntegerType, nullable = false),
    StructField("fulfillment_service", StringType, nullable = false),
    StructField("fulfillment_status", StringType, nullable = false),
    StructField("gift_card", BooleanType, nullable = false),
    StructField("grams", IntegerType, nullable = false),
    StructField("name", StringType, nullable = false),
    StructField("price", StringType, nullable = false),
    StructField("price_set", StructType(Seq(
      StructField("shop_money", StructType(Seq(
        StructField("amount", StringType, nullable = false),
        StructField("currency_code", StringType, nullable = false)
      )), nullable = false),
      StructField("presentment_money", StructType(Seq(
        StructField("amount", StringType, nullable = false),
        StructField("currency_code", StringType, nullable = false)
      )), nullable = false)
    )), nullable = false),
    StructField("product_exists", BooleanType, nullable = false),
    StructField("product_id", LongType, nullable = false),
    StructField("properties", ArrayType(StringType)),
    StructField("quantity", IntegerType, nullable = false),
    StructField("requires_shipping", BooleanType, nullable = false),
    StructField("sku", StringType, nullable = false),
    StructField("taxable", BooleanType, nullable = false),
    StructField("title", StringType, nullable = false),
    StructField("total_discount", StringType, nullable = false),
    StructField("total_discount_set", StructType(Seq(
      StructField("shop_money", StructType(Seq(
        StructField("amount", StringType, nullable = false),
        StructField("currency_code", StringType, nullable = false)
      )), nullable = false),
      StructField("presentment_money", StructType(Seq(
        StructField("amount", StringType, nullable = false),
        StructField("currency_code", StringType, nullable = false)
      )), nullable = false)
    )), nullable = false),
    StructField("variant_id", LongType, nullable = false),
    StructField("variant_inventory_management", StringType, nullable = false),
    StructField("variant_title", StringType),
    StructField("vendor", StringType, nullable = false),
    StructField("tax_lines", ArrayType(StructType(Seq(
      StructField("title", StringType, nullable = false),
      StructField("price", StringType, nullable = false),
      StructField("rate", DoubleType, nullable = false),
      StructField("price_set", StructType(Seq(
        StructField("shop_money", StructType(Seq(
          StructField("amount", StringType, nullable = false),
          StructField("currency_code", StringType, nullable = false)
        )), nullable = false),
        StructField("presentment_money", StructType(Seq(
          StructField("amount", StringType, nullable = false),
          StructField("currency_code", StringType, nullable = false)
        )), nullable = false)
      )), nullable = false),
      StructField("channel_liable", BooleanType, nullable = false)
    )))),
    StructField("duties", ArrayType(StructType(Seq.empty[StructField]))),
    StructField("discount_allocations", ArrayType(StructType(Seq(
      StructField("amount", StringType, nullable = false),
      StructField("discount_application_index", IntegerType, nullable = false),
      StructField("amount_set", StructType(Seq(
        StructField("shop_money", StructType(Seq(
          StructField("amount", StringType, nullable = false),
          StructField("currency_code", StringType, nullable = false)
        )), nullable = false),
        StructField("presentment_money", StructType(Seq(
          StructField("amount", StringType, nullable = false),
          StructField("currency_code", StringType, nullable = false)
        )), nullable = false)
      )), nullable = false)
    )))
    ))
  )



}
