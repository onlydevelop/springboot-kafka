depup:
	minikube kubectl -- apply -f k8s/dependencies.yml
depdown:
	minikube kubectl -- delete -f k8s/dependencies.yml
appup:
	minikube kubectl -- apply -f k8s/app.yml
appdown:
	minikube kubectl -- delete -f k8s/app.yml
allup:
	minikube kubectl -- apply -f k8s/dependencies.yml
	minikube kubectl -- apply -f k8s/app.yml
alldown:
	minikube kubectl -- delete -f k8s/app.yml
	minikube kubectl -- delete -f k8s/dependencies.yml
