package xyz.nabijaczleweli.gen_epub_book

import xyz.nabijaczleweli.gen_epub_book.book.{Book, load => loadElements}

object Main extends App {
	val settings = new Settings(args)
	val bookElements = loadElements(settings.inFile)
	val book = new Book(settings.inFile.getParent + "/", bookElements)
	println(book.name)
	println(book.cover)
	println(book.author)
	println(book.date)
	println(book.language)
	println(book.id)
	for(c <- book.content)
		println(c)
	for(nc <- book.nonContent)
		println(nc)
}
