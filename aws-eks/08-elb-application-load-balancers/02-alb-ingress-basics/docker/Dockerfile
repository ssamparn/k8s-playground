FROM nginx
COPY /html/index.html /app/index.html
COPY /conf/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]