package xyz.nabijaczleweli.gen_epub_book

import java.io.File

import scala.io.{Codec, Source}

package object book {
	def load(from: File, separator: String = ":"): Seq[Element] =
		(Source.fromFile(from)(Codec.UTF8).getLines flatMap (l => Element.parseLine(l, separator))).toList
}
