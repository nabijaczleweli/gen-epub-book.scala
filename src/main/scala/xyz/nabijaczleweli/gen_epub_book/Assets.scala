package xyz.nabijaczleweli.gen_epub_book

object Assets {
	val containerXMLContents = """<?xml version="1.0" encoding="UTF-8"?>
<container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
   <rootfiles>
      <rootfile full-path="content.opf" media-type="application/oebps-package+xml" />
   </rootfiles>
</container>
"""

	val contentOPFHeader = """<?xml version="1.0" encoding="UTF-8"?>
<package xmlns="http://www.idpf.org/2007/opf" unique-identifier="uuid" version="2.0">
  <metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:opf="http://www.idpf.org/2007/opf">
"""

	val mimeType = """application/epub+zip"""

	val stringContentHead = """<html xmlns="http://www.w3.org/1999/xhtml">
  <head></head>
  <body>
    """

	val stringContentTail = """
  </body>
</html>
"""
}
