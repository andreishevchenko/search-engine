package com.search.engine.service

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, onSuccess, parameter, parameters, path, post, _}
import akka.http.scaladsl.server.Route
import com.search.engine.model._
import com.search.engine.nodes.ShardNodeControllerComponent

import scala.concurrent.ExecutionContext.Implicits.global

trait ShardApi {
  this: ShardNodeControllerComponent =>

  lazy val shardRoute: Route = path("index") {
    get {
      parameter('search.as[String]) {
        search =>
          onSuccess(shardNodeController.search(search)) {
            keys =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, keys.map(_.value).mkString(",")))
          }
      }
    } ~
      post {
        parameters('tokens.as[String], 'key.as[String]) {
          (tokens, key) =>
            onSuccess(shardNodeController.put(tokens, Key(key))) {
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "ok"))
            }
        }
      }
  }
}
