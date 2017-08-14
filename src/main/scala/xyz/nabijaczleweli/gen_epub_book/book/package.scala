package xyz.nabijaczleweli.gen_epub_book

import java.io.File

import scala.io.Source

package object book {
	def load(from: File, separator: String = ":"): Seq[Element] =
		((Source fromFile from).getLines flatMap (l => Element.parseLine(l, separator))).toList
}
