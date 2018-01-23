package com.search.engine.nodes

import com.search.engine.model.{Key, Token}
import com.search.engine.store.IndexStore
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ShardNodeControllerSpec extends Specification with Mockito {

  lazy val indexStore1 = {
    val m = mock[IndexStore]
    m.put(any, any) returns Future.successful[Unit]()
    m.get(any) returns Future.successful(Seq(Key("k")))
    m
  }

  lazy val shardNodeController1 = new ShardNodeController(indexStore1)


  lazy val indexStore2 = {
    val m = mock[IndexStore]
    m.get(Token("t1")) returns Future.successful(Seq(Key("k1"), Key("k2")))
    m.get(Token("t2")) returns Future.successful(Seq(Key("k2"), Key("k3")))
    m
  }

  lazy val shardNodeController2 = new ShardNodeController(indexStore2)


  "ShardNodeController" should {

    "put all tokens to index store" in {

      Await.ready(shardNodeController1.put(Seq(Token("t1"), Token("t2")), Key("k")), 1.second)

      there was one(indexStore1).put(Token("t1"), Seq(Key("k")))
      there was one(indexStore1).put(Token("t2"), Seq(Key("k")))
    }

    "search result in index store and reduce it" in {
      val res = Await.result(shardNodeController2.search(Seq(Token("t1"), Token("t2"))), 1.second)

      there was one(indexStore2).get(Token("t1"))
      there was one(indexStore2).get(Token("t1"))

      res must be_==(Seq(Key("k1"), Key("k2"), Key("k3")))
    }
  }
}
