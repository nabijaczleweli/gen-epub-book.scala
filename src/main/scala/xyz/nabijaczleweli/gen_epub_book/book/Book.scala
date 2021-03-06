package xyz.nabijaczleweli.gen_epub_book.book

import java.io._
import java.net.HttpURLConnection
import java.nio.file.Files
import java.text.ParseException
import java.util.zip.{ZipEntry, ZipOutputStream}
import java.util.{Date, UUID}

import xyz.nabijaczleweli.gen_epub_book.feature.IncludeDirectory
import xyz.nabijaczleweli.gen_epub_book.{Assets, Util}

class Book(private val includeOrder: List[IncludeDirectory], private val elems: Seq[Element]) {
	private val id = UUID.randomUUID
	private var content: List[Content] = Nil
	private var nonContent: List[Content] = Nil

	var name: String = _
	var cover: Content = _
	var description: ContentData = _
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
				IncludeDirectory.find(includeOrder, p) match {
					case None => throw new ParseException(s"Content file '$p' not found.", 0)
					case Some(dir) => content :+= Content(dir ebookID p, dir ebookPath p, PathContent(dir resolve p))
				}
			case StringContentElement(s) =>
				content :+= Content(s"string-content-$idx", s"string-data-$idx.html", StringContent(s))
			case ImageContentElement(p) =>
				IncludeDirectory.find(includeOrder, p) match {
					case None => throw new ParseException(s"Image-Content file '$p' not found.", 0)
					case Some(dir) =>
						val fname = dir ebookPath p
						nonContent :+= Content(dir ebookID p, fname, PathContent(dir resolve p))
						content :+= Content(s"image-content-$idx", s"image-data-$idx.html", StringContent(
							s"""<center><img src="$fname" alt="$fname"></img></center>"""))
				}
			case NetworkImageContentElement(u) =>
				val fname = Util urlFilename u
				nonContent :+= Content(Util urlId u, Util urlFilename u, NetworkContent(u))
				content :+= Content(s"network-image-content-$idx", s"network-image-data-$idx.html",
					StringContent(s"""<center><img src="$fname" alt="$fname"></img></center>"""))
			case CoverElement(p) =>
				if(cover != null)
					throw new ParseException("[Network-]Cover key specified at least twice.", idx)
				IncludeDirectory.find(includeOrder, p) match {
					case None => throw new ParseException(s"Image-Content file '$p' not found.", 0)
					case Some(dir) =>
						val fname = dir ebookPath p
						nonContent :+= Content(dir ebookID p, fname, PathContent(dir resolve p))
						cover = Content(s"cover-content", s"cover-data.html", StringContent(
							s"""<center><img src="$fname" alt="$fname"></img></center>"""))
				}
			case NetworkCoverElement(u) =>
				if(cover != null)
					throw new ParseException("[Network-]Cover key specified at least twice.", idx)
				else {
					val fname = Util urlFilename u
					nonContent :+= Content(s"network-cover-${Util urlId u}", fname, NetworkContent(u))
					cover = Content(s"network-cover-content", s"network-cover-data.html", StringContent(
						s"""<center><img src="$fname" alt="$fname"></img></center>"""))
				}
			case IncludeElement(p) =>
				IncludeDirectory.find(includeOrder, p) match {
					case None => throw new ParseException(s"Image-Content file '$p' not found.", 0)
					case Some(dir) => nonContent :+= Content(dir ebookID p, dir ebookPath p, PathContent(dir resolve p))
				}
			case NetworkIncludeElement(u) =>
				nonContent :+= Content(Util urlId u, Util urlFilename u, NetworkContent(u))
			case DescriptionElement(p) =>
				if(description != null)
					throw new ParseException("[<String|Network>-]Description key specified at least twice.", idx)
				IncludeDirectory.find(includeOrder, p) match {
					case None => throw new ParseException(s"Description file '$p' not found.", 0)
					case Some(dir) =>
						description = PathContent(dir resolve p)
				}
			case StringDescriptionElement(s) =>
				if(description != null)
					throw new ParseException("[<String|Network>-]Description key specified at least twice.", idx)
				description = StringContent(s + '\n')
			case NetworkDescriptionElement(u) =>
				if(description != null)
					throw new ParseException("[<String|Network>-]Description key specified at least twice.", idx)
				description = NetworkContent(u)
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

	for((key, name) <- (name, "Name") :: (author, "Author") :: (date, "Date") :: (language, "Language") :: Nil)
		if(key	== null)
			throw new ParseException(s"Required key $name not specified", elems.length)

	((cover :: Nil) ++ content ++ nonContent).iterator filter (_ != null) foreach { elem =>
		if((Util guessMimeType elem.filename) == null)
			throw new ParseException(s"""File "${elem.filename}" is not of recognised extension.""", elems.length)
	}


	def write(to: File): Unit = {
		val out = new ZipOutputStream(new FileOutputStream(to))
		out setLevel 9

		out putNextEntry new ZipEntry("mimetype")
		out write Assets.mimeType.getBytes
		out.closeEntry()

		out putNextEntry new ZipEntry("META-INF/container.xml")
		out write Assets.containerXMLContents.getBytes
		out.closeEntry()

		writeContentTable(out)
		writeTOC(out)
		writeContent(out)

		out.close()
	}

	private def writeContentTable(to: ZipOutputStream): Unit = {
		val n = '\n'

		to putNextEntry new ZipEntry("content.opf")

		to write Assets.contentOPFHeader.getBytes
		to write s"""    <dc:title>$name</dc:title>$n""".getBytes
		to write s"""    <dc:creator opf:role="aut">$author</dc:creator>$n""".getBytes
		to write s"""    <dc:identifier id="uuid" opf:scheme="uuid">$id</dc:identifier>$n""".getBytes
		to write s"""    <dc:date>${Util.dateFormat format date}</dc:date>$n""".getBytes
		to write s"""    <dc:language>$language</dc:language>$n""".getBytes

		if(cover != null)
			to write s"""    <meta name="cover" content="${cover.id}" />$n""".getBytes

		if(description != null) {
			to write s"""    <dc:language>$n""".getBytes
			writeContentData(description, to, string_border = false)
			to write s"""    </dc:language>$n""".getBytes
		}

		to write s"""  </metadata>$n""".getBytes
		to write s"""  <manifest>$n""".getBytes
		to write s"""    <item href="toc.ncx" id="toc" media-type="application/x-dtbncx+xml" />$n""".getBytes

		var specIds: Set[String] = Nil.toSet
		((cover :: Nil) ++ content ++ nonContent).iterator filter (_ != null) filter (e => !specIds.contains(e.id)) foreach { elem =>
			to write s"""    <item href="${elem.filename}" id="${elem.id}" media-type="${Util guessMimeType elem.filename}" />$n""".getBytes
			specIds += elem.id
		}

		to write s"""  </manifest>$n""".getBytes
		to write s"""  <spine toc="toc">$n""".getBytes

		content foreach { elem =>
			to write s"""    <itemref idref="${elem.id}" />$n""".getBytes
		}

		to write s"""  </spine>$n""".getBytes
		to write s"""  <guide>$n""".getBytes

		if(cover != null)
			to write s"""    <reference xmlns="http://www.idpf.org/2007/opf" href="${cover.filename}" title="${cover.id}" type="cover" />$n""".getBytes

		to write s"""    <reference href="toc.ncx" title="Table of Contents" type="toc" />$n""".getBytes
		to write s"""  </guide>$n""".getBytes
		to write s"""</package>$n""".getBytes

		to.closeEntry()
	}

	private def writeTOC(to: ZipOutputStream): Unit = {
		val n = '\n'

		to putNextEntry new ZipEntry("toc.ncx")

		to write s"""<?xml version='1.0' encoding='utf-8'?>$n""".getBytes
		to write s"""<ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1" xml:lang="$language">$n""".getBytes
		to write s"""  <head>$n""".getBytes
		to write s"""    <meta content="$id" name="dtb:uid"/>$n""".getBytes
		to write s"""    <meta content="2" name="dtb:depth"/>$n""".getBytes
		to write s"""  </head>$n""".getBytes
		to write s"""  <docTitle>$n""".getBytes
		to write s"""    <text>$name</text>$n""".getBytes
		to write s"""  </docTitle>$n""".getBytes
		to write s"""  <navMap>$n""".getBytes

		(content.iterator filter (_.content.isInstanceOf[PathContent])
			flatMap (e => Util findTitle e.content.asInstanceOf[PathContent].path map ((_, e.id, e.filename)))).zipWithIndex foreach { e =>
			val ((title, _, fname), i) = e
			to write s"""    <navPoint id="$title" playOrder="$i">$n""".getBytes
			to write s"""      <navLabel>$n""".getBytes
			to write s"""        <text>$title</text>$n""".getBytes
			to write s"""      </navLabel>$n""".getBytes
			to write s"""      <content src="$fname" />$n""".getBytes
			to write s"""    </navPoint>$n""".getBytes
		}

		to write s"""  </navMap>$n""".getBytes
		to write s"""</ncx>$n""".getBytes

		to.closeEntry()
	}

	private def writeContent(to: ZipOutputStream): Unit = {
		var specFilenames: Set[String] = Nil.toSet
		((cover :: Nil) ++ content ++ nonContent).iterator filter (_ != null) filter (e => !specFilenames.contains(e.filename)) foreach { elem =>
			to putNextEntry new ZipEntry(elem.filename)
			writeContentData(elem.content, to)
			to.closeEntry()
			specFilenames += elem.filename
		}
	}

	private def writeContentData(whom: ContentData, to: OutputStream, string_border: Boolean = true): Unit = {
		whom match {
			case PathContent(p) =>
				Files.copy(p.toPath, to)
			case NetworkContent(u) =>
				val connection = u.openConnection().asInstanceOf[HttpURLConnection]
				connection.setRequestMethod("GET")
				val istream = connection.getInputStream

				val buf = new Array[Byte](4096)
				var read = istream read buf
				while(read != -1) {
					to write buf
					read = istream read buf
				}
			case StringContent(d) =>
				if(string_border)
					to write Assets.stringContentHead.getBytes
				to write d.getBytes
				if(string_border)
					to write Assets.stringContentTail.getBytes
		}
	}
}
