# This is a wrapper Dockerfile that builds the backend image
# For the actual backend build, see backend/Dockerfile

FROM scratch
COPY . /tmp/src
WORKDIR /tmp/src/backend
CMD ["echo", "Please use the Dockerfile in the backend directory for building the backend image"]
