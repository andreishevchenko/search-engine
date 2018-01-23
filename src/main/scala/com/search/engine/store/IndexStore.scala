package com.search.engine.store

import com.search.engine.model.{Key, Token}

import scala.concurrent.Future

trait IndexStore {

  def put(token: Token, keys: Seq[Key]): Future[Unit]

  def get(token: Token): Future[Seq[Key]]
}
