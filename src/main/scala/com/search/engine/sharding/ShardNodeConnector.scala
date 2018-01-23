package com.search.engine.sharding

import com.search.engine.model.{Key, Token}

import scala.concurrent.Future

trait ShardNodeConnector {

  def put(tokens: Seq[Token], key: Key): Future[Unit]

  def search(tokens: Seq[Token]): Future[Seq[Key]]
}