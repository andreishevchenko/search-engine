package com.search.engine.store

import java.util.concurrent.ConcurrentHashMap

import com.search.engine.model.{Document, Key}

import scala.concurrent.Future

class DocumentStoreInMemory extends DocumentStore {

  private val store = new ConcurrentHashMap[Key, Document]()

  override def put(key: Key, document: Document): Future[Unit] = Future.successful(store.put(key, document))

  override def get(key: Key): Future[Document] = Future.successful(store.get(key))
}
