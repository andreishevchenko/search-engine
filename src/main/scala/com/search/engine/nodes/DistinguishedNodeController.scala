package com.search.engine.nodes

import com.search.engine.model._
import com.search.engine.sharding.ShardController
import com.search.engine.store.DocumentStore

import scala.concurrent.{ExecutionContext, Future}

class DistinguishedNodeController(documentStore: DocumentStore, shardController: ShardController) {

  def put(key: Key, document: Document)(implicit ex: ExecutionContext): Future[Unit] =
    for {
      _ <- documentStore.put(key, document)
      u <- shardController.put(key, document)
    } yield u

  def get(key: Key): Future[Document] = documentStore.get(key)

  def search(query: String): Future[Seq[Key]] = shardController.search(query)
}
