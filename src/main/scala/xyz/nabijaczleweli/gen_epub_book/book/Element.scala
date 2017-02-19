package xyz.nabijaczleweli.gen_epub_book.book

import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

sealed trait Element
case class NameElement(n: String) extends Element
case class ContentElement(p: String) extends Element
case class StringContentElement(s: String) extends Element
case class ImageContentElement(p: String) extends Element
case class NetworkImageContentElement(u: URL) extends Element
case class CoverElement(p: String) extends Element
case class NetworkCoverElement(u: URL) extends Element
case class AuthorElement(a: String) extends Element
case class DateElement(d: Date) extends Element
case class LanguageElement(l: String) extends Element

object Element {
	private val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

	def parseLine(line: String): Option[Element] = {
		val chunks = line.split(":", 2)
		if(chunks.length < 2)
			None
		else {
			val ch = chunks.iterator map (c => c.trim)
			Some(ch.next match {
				case "Name" => NameElement(ch.next)
				case "Content" => ContentElement(ch.next)
				case "String-Content" => StringContentElement(ch.next)
				case "Image-Content" => ImageContentElement(ch.next)
				case "Network-Image-Content" => NetworkImageContentElement(new URL(ch.next))
				case "Cover" => CoverElement(ch.next)
				case "Network-Cover" => NetworkCoverElement(new URL(ch.next))
				case "Author" => AuthorElement(ch.next)
				case "Date" => DateElement(dateFormat.parse(ch.next))
				case "Language" => LanguageElement(ch.next)
				case _ => return None
			})
		}
	}
}
