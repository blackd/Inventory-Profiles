#!/usr/bin/env python3
#-*- coding: utf-8 -*-

import pypandoc
from premailer import transform

with open("output.html", "wb") as output:
    output.write(
        transform(
            pypandoc.convert_file(
                "description.md", 
                "html", 
                format="md"
            ), 
            allow_loading_external_files=True,
            external_styles=["style.css"],
            remove_classes=True
        )
        .replace("<html><head></head><body>", "")
        .replace("</body></html>", "")
        .encode("utf-8")
    )
