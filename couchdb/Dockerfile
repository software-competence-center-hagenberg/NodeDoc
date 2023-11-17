FROM couchdb:2.3.1

RUN echo "[cors]\norigins = *\ncredentials = true\nmethods = GET, PUT, POST, HEAD, DELETE\nheaders = accept, authorization, content-type, origin, referer\n\n[httpd]\nenable_cors = true" > /opt/couchdb/etc/local.d/docker.ini