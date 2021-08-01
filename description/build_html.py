#!/usr/bin/env python3
#-*- coding: utf-8 -*-

import pypandoc
from premailer import transform as premail
from pathlib import Path
import re

def main():
    Path("out/debug").mkdir(parents=True, exist_ok=True)

    html = pypandoc.convert_file(
        "description.md",
        "html",
        format="md",
        filters=["pandoc-include"]
    )

    with open("out/debug/pandoc.html", "wb") as f:
        f.write(html.encode("utf-8"))

    premailed = premail(
        html,
        allow_loading_external_files=True,
        external_styles=["style.css"],
        remove_classes=False
    )

    with open("out/debug/premail.html", "wb") as f:
        f.write(premailed.encode("utf-8"))

    output = (
        re.sub(
            "<!-- .+ -->\n",
            "",
            premailed
        )
        .replace("<html><head></head><body>", "")
        .replace("</body></html>", "")
        .replace("<h1", "<p></p>\n<h1")
        .replace("<h2", "<p></p>\n<h2")
        .replace("<h3", "<p></p>\n<h3")
        .replace("</h1>", "</h1>\n<p></p>")
        .replace("</h2>", "</h2>\n<p></p>")
        .replace("</h3>", "</h3>\n<p></p>")
        .replace("<p></p>\n<p></p>", "<p></p>")
    )

    with open("out/output.html", "wb") as f:
        f.write(output.encode("utf-8"))

if __name__ == "__main__":
    main()
