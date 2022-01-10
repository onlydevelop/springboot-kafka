depup:
	kubectl apply -f k8s/dependencies.yaml
depdown:
	kubectl delete -f k8s/dependencies.yaml
appup:
	kubectl apply -f k8s/app.yaml
appdown:
	kubectl delete -f k8s/app.yaml
allup:
	kubectl apply -f k8s/dependencies.yaml
	kubectl apply -f k8s/app.yaml
alldown:
	kubectl delete -f k8s/app.yaml
	kubectl delete -f k8s/dependencies.yaml