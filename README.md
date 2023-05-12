# Shopify-Sales-ERP
This is an ETL pipeline that handles the data warehousing portion of an ERP system for a Shopify Store. The data warehouse holds the data for the fulfilled orders from a Shopify store.

This ETL pipeline was built with Scala, Amazon Web Services, Apache Kafka, Apache Spark, and Apache Cassandra.

**Below is a diagram and the steps for the ETL pipeline:**

<img width="946" alt="PNG image" src="https://github.com/AnantaMoharana/Shopify-Sales-ERP/assets/48960503/62b479fe-e860-4355-80b6-c45abfd138ad">

**Extract:**
In this step, we extract the data from Shopify using a Shopify webhook, Amazon Web Services Eventbridge, and Amazon Web Services Lambda. When an order is fulfilled in Shopify a webhook is sent to an endpoint hosted on Amazon Web Services Eventbridge. The webhooks payload is then extracted using an Amazon Web Services Lambda function which then stores the data in an Apache Kafak topic hosted on an Amazon Web Services EC2 machine. 

**Transform:**
In this step, we tranform the data before loading it into our data warehouse. We do this by using a Spark stream that reads and processes the data from Kafka as it arrives in real time. The data is read using a read strem, stored in a Spark dataframe, and then gets transformed into the respective tables

**Load:**
In this step, we load the data tables into our Apache Cassandra data warehouse using a write stream from Apache Spark. 
