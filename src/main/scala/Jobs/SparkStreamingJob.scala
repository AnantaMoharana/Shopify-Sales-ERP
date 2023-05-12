package Jobs
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.log4j.{Level, Logger}

//Create an abstract class that will serve as the base for our Spark Streaming job
//Takes in the what the name of the streaming app and the master will determine where the job runs
abstract class SparkStreamingJob(appName:String, master:String) {

//  This method extracts the data from Kafka by creating the Spark session that will read from it
//  It takes in topicName, the name of the kafka topic, and kafIp, the ip address of where the kafka topic is stored
  def extract(topicName:String,kafkaIp:String): DataFrame ={
//  This will ensure that when the streaming job is running only warning level messages are logged
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("spark").setLevel(Level.WARN)

//  Creates a new Spark streaming session
    val spark = SparkSession.builder()
      .appName(appName)
      .master(master)
      .getOrCreate()

//  This reads the data from Kafka via the Spark stream and loads each new record to a Spark dataframe
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"${kafkaIp}:9092")
      .option("subscribe", s"${topicName}")
      .load()

    return df

  }

//This method runs the full ETL pipeline, it is to be filled in by which ever Scala class extends it
  def runETL(dataFrame: DataFrame) : Unit ={

  }


}
