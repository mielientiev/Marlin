package com.marlin.helpers.mongodb

import java.io.{File, InputStream}

import scala.io.Source

trait MongoRequestBuilding {

  def LoadJson(json: String, database: String, collection: String) = SeqMongoRequest(json, database, collection)
  def LoadJson(json: String, collection: String) = SeqMongoRequest(json, collection)
  def LoadJson(json: String) = SeqMongoRequest(json)

  def LoadFromFile(fileLocation: String) = SeqMongoRequest(new File(fileLocation))
  def LoadFromFile(fileLocation: String, collection: String) = SeqMongoRequest(new File(fileLocation), collection)
  def LoadFromFile(fileLocation: String, database: String, collection: String) = SeqMongoRequest(new File(fileLocation), database, collection)

  def LoadFromResource(fileLocation: String) = SeqMongoRequest(resourceStream(fileLocation))
  def LoadFromResource(fileLocation: String, collection: String) = SeqMongoRequest(resourceStream(fileLocation), collection)
  def LoadFromResource(fileLocation: String, database: String, collection: String) = SeqMongoRequest(resourceStream(fileLocation), database, collection)
  def ClearAfterTest(database: String, collection: String) = SeqMongoRequest("{}", database, collection)

  private def resourceStream(resourceName: String): InputStream = {
    val is = getClass.getClassLoader.getResourceAsStream(resourceName)
    require(is ne null, s"Resource $resourceName not found")
    is
  }

  final class SeqMongoRequest(val requests: Seq[MongoRequest])
  final class MongoRequest(val jsonList: Seq[String], val database: String, val collection: String)

  object SeqMongoRequest {

    private val DefaultDatabase = "test"
    private val DefaultCollection = "test"

    def apply(json: String): SeqMongoRequest = apply(json, DefaultDatabase, DefaultCollection)
    def apply(json: String, collection: String): SeqMongoRequest = apply(json, DefaultDatabase, collection)
    def apply(json: String, database: String, collection: String): SeqMongoRequest =
      SeqMongoRequest(new MongoRequest(List(json), database, collection))


    def apply(file: File): SeqMongoRequest = apply(file, DefaultDatabase, DefaultCollection)
    def apply(file: File, collection: String): SeqMongoRequest = apply(file, DefaultDatabase, collection)
    def apply(file: File, database: String, collection: String): SeqMongoRequest = {
      val json = Source.fromFile(file).getLines().toList
      SeqMongoRequest(new MongoRequest(json, database, collection))
    }


    def apply(is: InputStream): SeqMongoRequest = apply(is, DefaultDatabase, DefaultCollection)
    def apply(is: InputStream, collection: String): SeqMongoRequest = apply(is, DefaultDatabase, collection)
    def apply(is: InputStream, database: String, collection: String): SeqMongoRequest = {
      val json = Source.fromInputStream(is).getLines().toList
      SeqMongoRequest(new MongoRequest(json, database, collection))
    }


    def apply(first: MongoRequest) = new SeqMongoRequest(List(first))
    def apply(first: SeqMongoRequest, second: SeqMongoRequest) = new SeqMongoRequest(first.requests ++ second.requests)
    def apply(head: MongoRequest, tail: SeqMongoRequest) =  new SeqMongoRequest(head+:tail.requests)

  }

}

