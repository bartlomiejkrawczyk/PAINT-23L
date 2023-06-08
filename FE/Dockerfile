FROM httpd:2.4-alpine
RUN echo "ServerName localhost" >> /usr/local/apache2/conf/httpd.conf
COPY ./public-html/ /usr/local/apache2/htdocs/
