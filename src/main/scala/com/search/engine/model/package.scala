package com.search.engine

package object model {

  case class Key(value: String)

  case class Token(value: String)

  case class Document(value: String)

  implicit def asTokens(separatedString: String): Seq[Token] = separatedString.split(",").map(Token.apply).toSeq
  implicit def asKeys(separatedString: String): Seq[Key] = separatedString.split(",").map(Key.apply).toSeq
}
