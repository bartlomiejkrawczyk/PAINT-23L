FROM httpd:2.4
RUN echo "ServerName localhost" >> /usr/local/apache2/conf/httpd.conf
COPY ./public-html/ /usr/local/apache2/htdocs/
