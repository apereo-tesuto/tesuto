docker stop $(docker ps -a -q)
docker container prune -f

for i in ./tesuto-activation-services/activation-microservice ./tesuto-content-services/tesuto-content-microservice ./tesuto-delivery-services/tesuto-delivery-microservice ./tesuto-qti-importer-services/tesuto-qti-importer-microservice ./tesuto-admin-services/tesuto-admin-microservice ./tesuto-placement-services/tesuto-placement-microservice ./tesuto-rules-services/tesuto-rules-microservice
do
	cd ${i}
	mvn clean install spring-boot:repackage -D maven.test.skip=true
	cd ../..
done

cd ./tesuto-router
mvn clean install spring-boot:repackage -D maven.test.skip=true

cd ..

cd ./tesuto-ui
mvn clean install spring-boot:repackage -D maven.test.skip=true -Plibs

cd ..

cd ./tesuto-activation-services
docker image rm tesuto-activation
docker image build -t tesuto-activation .

cd ..

cd ./tesuto-content-services
docker image rm tesuto-content
docker image build -t tesuto-content .

cd ..

cd ./tesuto-delivery-services
docker image rm tesuto-delivery
docker image build -t tesuto-delivery .

cd ..

cd ./tesuto-qti-importer-services
docker image rm tesuto-qti-importer
docker image build -t tesuto-qti-importer .

cd ..

cd ./tesuto-admin-services
docker image rm tesuto-admin
docker image build -t tesuto-admin .

cd ..

cd ./tesuto-router
docker image rm tesuto-router
docker image build -t tesuto-router .

cd ..

cd ./tesuto-ui
docker image rm tesuto-ui
docker image build -t tesuto-ui .

cd ..

cd ./tesuto-placement-services
docker image rm tesuto-placement
docker image build -t tesuto-placement .

cd ..

cd ./tesuto-rules-services
docker image rm tesuto-rules
docker image build -t tesuto-rules .

cd ..

cd ./mongo
docker image rm tesuto-mongo
docker image build -t tesuto-mongo .

cd ..

cd ./elasticmq
docker image rm tesuto-elasticmq
docker image build -t tesuto-elasticmq .

cd ..

docker-compose up -d