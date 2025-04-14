# Makefile for CertVault build process

# Variables
FRONTEND_DIR = frontend
BACKEND_DIR = server
STATIC_DIR = $(BACKEND_DIR)/src/main/resources/static
DIST_DIR = $(FRONTEND_DIR)/dist
JAR_SOURCE = $(BACKEND_DIR)/target/*.jar
JAR_DEST = ./certvault.jar
INSTALL_DIR = /etc/certvault
SERVICE_FILE = certvault.service
SYSTEMD_DIR = /etc/systemd/system

.PHONY: all clean install uninstall

all: frontend-build copy-frontend backend-build move-jar

frontend-build:
	@echo "Building frontend..."
	cd $(FRONTEND_DIR) && pnpm install && pnpm run build

copy-frontend:
	@echo "Copying frontend resources..."
	mkdir -p $(STATIC_DIR)
	cp -r $(DIST_DIR)/* $(STATIC_DIR)/

backend-build:
	@echo "Building backend..."
	mvn clean package -f $(BACKEND_DIR)/pom.xml -DskipTests
	chmod +x $(JAR_SOURCE)

move-jar:
	@echo "Moving JAR file..."
	mv $(JAR_SOURCE) $(JAR_DEST)

clean:
	@echo "Cleaning up..."
	rm -rf $(DIST_DIR)
	rm -rf $(STATIC_DIR)
	rm -f $(JAR_DEST)
	mvn clean -f $(BACKEND_DIR)/pom.xml

install:
	@echo "Installing CertVault service..."
	@# Create installation directory
	sudo mkdir -p $(INSTALL_DIR)

	@# Install application files
	sudo install -m 644 $(JAR_DEST) $(INSTALL_DIR)/certvault.jar
	sudo install -m 644 application.yml $(INSTALL_DIR)/application.yml

	@# Create systemd service file
	@echo "[Unit]\n\
	Description=CertVault Certificate Management Service\n\
	After=network.target\n\n\
	[Service]\n\
	User=root\n\
	WorkingDirectory=$(INSTALL_DIR)\n\
	ExecStart=/usr/bin/java -jar $(INSTALL_DIR)/certvault.jar \n\
		-Xmx512m \n\
		-Xms256m \n\
		-XX:+UseZGC \n\
		-XX:ZCollectionInterval=120 \n\
		-XX:ZAllocationSpikeTolerance=4 \n\
		-XX:-ZProactive \n\
		-XX:+HeapDumpOnOutOfMemoryError \n\
		-XX:HeapDumpPath=./errorDump.hprof \n\
		--spring.profiles.active=prod \n\
	SuccessExitStatus=143\n\
	Restart=always\n\
	RestartSec=30\n\
	[Install]\n\
	WantedBy=multi-user.target" | sudo tee $(SYSTEMD_DIR)/$(SERVICE_FILE) > /dev/null

	@# Reload and enable service
	sudo systemctl daemon-reload
	sudo systemctl enable $(SERVICE_FILE)
	sudo systemctl start $(SERVICE_FILE)
	@echo "Installation completed. Service is now running."

uninstall:
	@echo "Uninstalling CertVault service..."
	sudo systemctl stop $(SERVICE_FILE) || true
	sudo systemctl disable $(SERVICE_FILE) || true
	sudo rm -f $(SYSTEMD_DIR)/$(SERVICE_FILE)
	sudo rm -rf $(INSTALL_DIR)
	sudo systemctl daemon-reload
	@echo "Uninstallation completed."

# Shortcut targets
build: all
jar: backend-build move-jar