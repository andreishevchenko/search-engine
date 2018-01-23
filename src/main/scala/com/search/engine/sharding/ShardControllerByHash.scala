package com.search.engine.sharding

import com.search.engine.model.{Document, Key, Token}

import scala.concurrent.{ExecutionContext, Future}

class ShardControllerByHash(shards: Map[Int, ShardNodeConnector])(implicit ex: ExecutionContext) extends ShardController {

  def put(key: Key, document: Document): Future[Unit] = {
    mapReduce(tokens(document))(
      mapFunction = (nodeNumber, tokens) => shards(nodeNumber).put(tokens, key),
      reduceFunction = (_: Iterable[Unit]) => ()
    )
  }

  def search(query: String): Future[Seq[Key]] = {
    mapReduce(tokens(query))(
      mapFunction = (nodeNumber, tokens) => shards(nodeNumber).search(tokens),
      reduceFunction =
        (seqOfSeq: Iterable[Seq[Key]]) => seqOfSeq.reduce(_ ++ _).distinct
    )
  }

  protected[this] def mapReduce[R](tokensFunction: => Seq[Token])
                                  (mapFunction: (Int, Seq[Token]) => Future[R],
                                   reduceFunction: Iterable[R] => R): Future[R] = {
    Future
      .successful(groupByNode(tokensFunction))
      .flatMap {
        nodeToTokens =>
          val futures = nodeToTokens.map {
            case (nodeNumber, tokens) =>
              mapFunction(nodeNumber, tokens)
          }

          Future
            .sequence(futures)
            .map(reduceFunction)
      }

  }


  protected[this] def groupByNode(tokens: Seq[Token]): Map[Int, Seq[Token]] = {
    tokens
      .distinct
      .foldLeft(Map.empty[Int, Seq[Token]]) {
        case (map, token) =>
          val node = nodeNumber(token)
          val tokens = map.getOrElse(node, List.empty[Token])
          val updatedTokens = tokens.+:(token)
          map.+(node -> updatedTokens)
      }
  }

  protected def tokens(document: Document): Seq[Token] = tokens(document.value)

  protected def tokens(value: String): Seq[Token] = value.split("\\s+").map(Token).toList

  protected def nodeNumber(token: Token): Int = Math.abs(token.value.hashCode % shards.size)
}
