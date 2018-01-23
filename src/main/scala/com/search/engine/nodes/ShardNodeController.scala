package com.search.engine.nodes


import com.search.engine.model.{Key, Token}
import com.search.engine.store.IndexStore

import scala.concurrent.{ExecutionContext, Future}

class ShardNodeController(indexStore: IndexStore) {


  def put(tokens: Seq[Token], key: Key)(implicit ex: ExecutionContext): Future[Unit] = {
    val updateFutures =
      tokens.map {
        token =>
          indexStore
            .get(token)
            .map(keys => keys.+:(key).distinct)
            .flatMap(updatedKeys => indexStore.put(token, updatedKeys))
      }

    Future
      .sequence(updateFutures)
      .map(_ => ())
  }

  def search(tokens: Seq[Token])(implicit ex: ExecutionContext): Future[Seq[Key]] = {
    val resultFutures = tokens.map(token => indexStore.get(token))
    Future
      .sequence(resultFutures)
      .map(seqOfResults =>
        seqOfResults
          .reduce(_ ++ _)
          .distinct)
  }
}
