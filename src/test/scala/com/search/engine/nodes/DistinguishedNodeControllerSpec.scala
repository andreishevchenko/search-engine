package com.search.engine.nodes

import com.search.engine.model._
import com.search.engine.sharding.ShardController
import com.search.engine.store.DocumentStore
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


class DistinguishedNodeControllerSpec extends Specification {

  lazy val documentStore = new DocumentStore {
    override def put(key: Key, document: Document): Future[Unit] = Future.successful[Unit]()
    override def get(key: Key): Future[Document] = if (key.value == "key1") Future.successful(Document("doc")) else Future.failed(new Exception)
  }

  lazy val shardController = new ShardController {
    override def put(key: Key, document: Document): Future[Unit] = Future.successful[Unit]()
    override def search(query: String): Future[Seq[Key]] = if (query == "search1") Future.successful(Seq(Key("key1"))) else Future.failed(new Exception)
  }

  lazy val nodeController = new DistinguishedNodeController(documentStore, shardController)

  "DistinguishedNodeController" should {

    "complete put when documentStore and shardController finished their process" in {
      Await.result(nodeController.put(Key("key"), Document("doc")), 1.second) must not throwA()
    }

    "get document from document store by a key" in {
      Await.result(nodeController.get(Key("key1")), 1.second) must be_==(Document("doc"))
    }

    "return search result from shard controller" in {
      Await.result(nodeController.search("search1"), 1.second) must be_==(Seq(Key("key1")))
    }
  }
}
