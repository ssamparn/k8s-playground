FROM nginx
COPY /html /etc/nginx/html/app/v3
COPY /conf/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]