object Main {
  def main(args: Array[String]): Unit = {
    //Initialize an instance of the ShopifyOrderETL class
    //Name it "Completed Order Processing" and run it using the desired place
    val fulfilledOrderETL = new ShopifyOrderETL("Completed Order Processing", "local[*]")

    //Extract the data from the kafka topic "fulfilled-orders" stored on the given IP address
    //This starts the read stream that reads the data and stores it
    val data = fulfilledOrderETL.extract("fulfilled-orders","<Redacted>")

    //Pass dataframe from the read stream  to be transformed and loaded with a write stream
    fulfilledOrderETL.runETL(data)

  }

}
