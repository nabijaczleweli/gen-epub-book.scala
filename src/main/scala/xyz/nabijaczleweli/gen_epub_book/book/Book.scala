package xyz.nabijaczleweli.gen_epub_book.book

import java.io.File
import java.text.ParseException
import java.util.{Date, UUID}

import xyz.nabijaczleweli.gen_epub_book.Util

class Book(private val relativeDirectoryRoot: String, private val elems: Seq[Element]) {
	val id = UUID.randomUUID
	var content: List[Content] = Nil
	var nonContent: List[Content] = Nil

	var name: String = _
	var cover: Content = _
	var author: String = _
	var date: Date = _
	var language: String = _

	for((elem, idx) <- elems.iterator.zipWithIndex)
		elem match {
			case NameElement(n) =>
				if(name != null)
					throw new ParseException("Key 'Name' specified twice", idx)
				else
					name = n
			case ContentElement(p) =>
				val c = new File(relativeDirectoryRoot + p)
				if(!c.exists || !c.isFile)
					throw new ParseException(s"Content file '$p' nonexistant.", 0)
				else
					content :+= Content(Util pathId p, Util pathFilename p, PathContent(c))
			case StringContentElement(s) =>
				content :+= Content(s"string-content-$idx", s"string-data-$idx.html", StringContent(s))
			case ImageContentElement(p) =>
				val c = new File(relativeDirectoryRoot + p)
				if(!c.exists || !c.isFile)
					throw new ParseException(s"Image-Content file '$p' nonexistant.", 0)
				else {
					nonContent :+= Content(Util pathId p, Util pathFilename p, PathContent(c))
					content :+= Content(s"image-content-$idx", s"image-data-$idx.html", StringContent(s"""<center><img src="${Util pathFilename p}"></img></center>"""))
				}
			case NetworkImageContentElement(u) =>
				nonContent :+= Content(Util urlId u, Util urlFilename u, NetworkContent(u))
				content :+= Content(s"network-image-content-$idx", s"network-image-data-$idx.html",
					StringContent(s"""<center><img src="${Util urlFilename u}"></img></center>"""))
			case CoverElement(p) =>
				if(cover != null)
					throw new ParseException("[Network-]Cover key specified at least twice.", idx)
				val c = new File(relativeDirectoryRoot + p)
				if(!c.exists || !c.isFile)
					throw new ParseException(s"Cover file '$p' nonexistant.", 0)
				else
					cover = Content(Util pathId p, Util pathFilename p, PathContent(c))
			case NetworkCoverElement(u) =>
				if(cover != null)
					throw new ParseException("[Network-]Cover key specified at least twice.", idx)
				else
					cover = Content(s"network-cover-${Util urlId u}", Util urlFilename u, NetworkContent(u))
			case AuthorElement(l) =>
				if(author != null)
					throw new ParseException("Author key specified at least twice.", idx)
				else
					author = l
			case DateElement(d) =>
				if(date != null)
					throw new ParseException("Date key specified at least twice.", idx)
				else
					date = d
			case LanguageElement(l) =>
				if(language != null)
					throw new ParseException("Language key specified at least twice.", idx)
				else
					language = l
		}

	if(name	== null)
		throw new ParseException("Required key Name not specified", elems.length)
	if(author	== null)
		throw new ParseException("Required key Author not specified", elems.length)
	if(date	== null)
		throw new ParseException("Required key Date not specified", elems.length)
	if(language== null)
		throw new ParseException("Required key Language not specified", elems.length)
}
