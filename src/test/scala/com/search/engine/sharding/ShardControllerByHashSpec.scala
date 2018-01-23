package com.search.engine.sharding

import com.search.engine.model.{Document, Key, Token}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ShardControllerByHashSpec extends Specification with Mockito {

  class Context extends Scope {
    lazy val shard0 = {
      val m = mock[ShardNodeConnector]
      m.put(any, any) returns Future.successful[Unit]()
      m.search(Seq(Token("d"))) returns Future.successful(Seq(Key("k1"), Key("k2")))
      m
    }

    lazy val shard1 = {
      val m = mock[ShardNodeConnector]
      m.put(any, any) returns Future.successful[Unit]()
      m.search(Seq(Token("ds"))) returns Future.successful(Seq(Key("k2"), Key("k3")))
      m
    }

    lazy val shards = Map(0 -> shard0, 1 -> shard1)

    lazy val shardController = new ShardControllerByHash(shards)
  }

  "ShardControllerByHash" should {

    "put key in shard by hash" in new Context {
      Await.ready(shardController.put(Key("k1"), Document("d")), 1.second)

      // "d".hashCode % 2 == 0 - shard0
      there was one(shard0).put(Seq(Token("d")), Key("k1"))
      there was no(shard1).put(any, any)
    }

    "search in shard selected by hash" in new Context {
      val res = Await.result(shardController.search("d"), 1.second)

      there was one(shard0).search(Seq(Token("d")))
      there was no(shard1).search(any)
      res must be_==(Seq(Key("k1"), Key("k2")))
    }

    "search in shards and merge results" in new Context {
      val res = Await.result(shardController.search("d ds"), 1.second)

      there was one(shard0).search(Seq(Token("d")))
      there was one(shard1).search(Seq(Token("ds")))
      res must be_==(Seq(Key("k1"), Key("k2"), Key("k3")))
    }
  }
}
