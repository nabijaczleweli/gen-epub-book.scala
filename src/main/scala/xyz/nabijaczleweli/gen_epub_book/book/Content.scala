package xyz.nabijaczleweli.gen_epub_book.book

import java.net.URL

case class Content(id: String, filename: String, content: ContentData)

sealed trait ContentData
case class PathContent(path: String) extends ContentData
case class NetworkContent(url: URL) extends ContentData
case class StringContent(data: String) extends ContentData
