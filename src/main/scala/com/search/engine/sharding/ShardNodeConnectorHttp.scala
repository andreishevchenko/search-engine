package com.search.engine.sharding

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.search.engine.model._

import scala.concurrent.{ExecutionContextExecutor, Future}

class ShardNodeConnectorHttp(shardUri: Uri)
                            (implicit val system: ActorSystem,
                             implicit val materializer: ActorMaterializer) extends ShardNodeConnector {

  private[this] implicit lazy val executionContext: ExecutionContextExecutor = system.dispatcher

  override def put(tokens: Seq[Token], key: Key): Future[Unit] = {
    Http()
      .singleRequest(
        HttpRequest(
          uri = shardUri.withPath(Path("/index")).withQuery(Uri.Query(Map("tokens" -> tokens.map(_.value).mkString(","), "key" -> key.value))),
          method = HttpMethods.POST))
      .map(_ => ())
  }

  override def search(tokens: Seq[Token]): Future[Seq[Key]] = {
    Http()
      .singleRequest(HttpRequest(uri = shardUri.withPath(Path("/index")).withQuery(Uri.Query(Map("search" -> tokens.map(_.value).mkString(","))))))
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          entity
            .dataBytes
            .runFold(ByteString(""))(_ ++ _)
            .map(_.utf8String)
            .map(asKeys)
        case _ =>
          Future.successful(Seq.empty[Key])
      }
  }
}
