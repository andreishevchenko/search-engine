package com.search.engine.nodes

import com.search.engine.model._

class DistinguishedNode {

  val nodesNumber = 2

  lazy val shardNodes: Map[Int, ShardNode] = {
    (0 until nodesNumber).foldLeft(Map.empty[Int, ShardNode]) {
      case (map, number) =>
        map.+(number -> new ShardNode)
    }
  }

  def put(key: Key, document: Document): Unit = {
    storeDocument(key, document)

    groupByNode(tokens(document)) foreach {
      case (node, tokens) =>
        putToNode(node, tokens, key) // TODO Futures
    }
  }

  def get(key: Key): Document = ???

  def search(query: String): List[Key] = {
    groupByNode(tokens(query))
      .map { case (node, tokens) => queryNode(node, tokens) } //TODO Futures
      .reduce(_ ++ _)
      .distinct
  }


  protected def tokens(document: Document): List[Token] = tokens(document.value)

  protected def tokens(value: String): List[Token] = value.split("\\s+").map(Token).toList

  protected def nodeNumber(token: Token): Int = Math.abs(token.value.hashCode % nodesNumber)

  protected def storeDocument(key: Key, document: Document): Unit = ???


  protected def groupByNode(tokens: List[Token]): Map[Int, List[Token]] = {
    tokens
      .distinct
      .foldLeft(Map.empty[Int, List[Token]]) {
        case (map, token) =>
          val node = nodeNumber(token)
          val tokens = map.getOrElse(node, List.empty[Token])
          val updatedTokens = tokens.+:(token)
          map.+(node -> updatedTokens)
      }
  }

  protected def putToNode(nodeNumber: Int, tokens: List[Token], key: Key): Unit = shardNodes(nodeNumber).put(tokens, key)

  protected def queryNode(nodeNumber: Int, tokens: List[Token]): List[Key] = shardNodes(nodeNumber).query(tokens)
}
