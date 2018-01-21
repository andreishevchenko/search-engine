package com.search.engine.nodes

import com.search.engine.model.{Key, Token}

import scala.collection.mutable

class ShardNode {

  val index = mutable.HashMap.empty[Token, List[Key]]

  def put(tokens: List[Token], key: Key): Unit = {
    tokens.foreach { // TODO parallel
      token =>
        val keyList = index.getOrElseUpdate(token, List.empty[Key])
        val updated = keyList.+:(key).distinct
        index.put(token, updated)
    }
  }

  def query(tokens: List[Token]): List[Key] = {
    tokens
      .map(token => index.getOrElse(token, List.empty[Key])) //TODO parallel
      .reduce(_ ++ _)
      .distinct
  }
}
