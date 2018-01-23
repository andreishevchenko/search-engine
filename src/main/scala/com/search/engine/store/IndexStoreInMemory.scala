package com.search.engine.store

import java.util.concurrent.ConcurrentHashMap

import com.search.engine.model.{Key, Token}

import scala.concurrent.Future

class IndexStoreInMemory extends IndexStore {

  private val index = new ConcurrentHashMap[Token, Seq[Key]]()

  override def put(token: Token, keys: Seq[Key]): Future[Unit] = Future.successful(index.put(token, keys))

  override def get(token: Token): Future[Seq[Key]] = Future.successful(index.getOrDefault(token, Seq.empty[Key]))
}
