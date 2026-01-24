FROM almalinux:8
RUN yum install nginx -y
RUN rm -rf /usr/share/nginx/html/index.html
RUN echo "<h1>Hello, I am from APP-1</h1>" > /usr/share/nginx/html/index.html
CMD ["nginx", "-g", "daemon off;"]
---
FROM almalinux:8
RUN yum install nginx -y
RUN rm -rf /usr/share/nginx/html/index.html
RUN echo "<h1>Hello, I am from APP-2</h1>" > /usr/share/nginx/html/index.html
CMD ["nginx", "-g", "daemon off;"]
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
	name: app1
	annotations:
		alb.ingress.kubernetes.io/scheme: internet-facing
		alb.ingress.kubernetes.io/target-type: ip
		alb.ingress.kubernetes.io/tags: Environment=dev, Project=roboshop
		alb.ingress.kubernetes.io/group.name: joindevops
spec: 
	ingressClassName: alb
	rules:
	- host: "app1.joindevops.online"
	  http:
	    paths:
	    - path: /
	      pathType: Prefix
	      backend:
		service:
		  name: app1
		  port:
		    number: 80