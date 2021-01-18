/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.airavata.mft.resource.server.backend.sql;

import org.apache.airavata.mft.resource.server.backend.ResourceBackend;
import org.apache.airavata.mft.resource.server.backend.sql.entity.*;
import org.apache.airavata.mft.resource.server.backend.sql.repository.*;
import org.apache.airavata.mft.resource.stubs.azure.resource.*;
import org.apache.airavata.mft.resource.stubs.azure.storage.*;
import org.apache.airavata.mft.resource.stubs.box.resource.*;
import org.apache.airavata.mft.resource.stubs.box.storage.*;
import org.apache.airavata.mft.resource.stubs.dropbox.resource.*;
import org.apache.airavata.mft.resource.stubs.dropbox.storage.*;
import org.apache.airavata.mft.resource.stubs.ftp.resource.*;
import org.apache.airavata.mft.resource.stubs.ftp.storage.*;
import org.apache.airavata.mft.resource.stubs.gcs.resource.*;
import org.apache.airavata.mft.resource.stubs.gcs.storage.*;
import org.apache.airavata.mft.resource.stubs.local.resource.*;
import org.apache.airavata.mft.resource.stubs.local.storage.*;
import org.apache.airavata.mft.resource.stubs.s3.resource.*;
import org.apache.airavata.mft.resource.stubs.s3.storage.*;
import org.apache.airavata.mft.resource.stubs.scp.resource.*;
import org.apache.airavata.mft.resource.stubs.scp.storage.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class SQLResourceBackend implements ResourceBackend {

    private static final Logger logger = LoggerFactory.getLogger(SQLResourceBackend.class);

    @Autowired
    private SCPStorageRepository scpStorageRepository;

    @Autowired
    private SCPResourceRepository scpResourceRepository;

    @Autowired
    private LocalResourceRepository localResourceRepository;

    @Autowired
    private FTPResourceRepository ftpResourceRepository;

    @Autowired
    private FTPStorageRepository ftpStorageRepository;

    private DozerBeanMapper mapper = new DozerBeanMapper();

    @Override
    public void init() {
        logger.info("Initializing database resource backend");
    }

    @Override
    public void destroy() {
        logger.info("Destroying database resource backend");
    }

    @Override
    public Optional<SCPStorage> getSCPStorage(SCPStorageGetRequest request) {
        Optional<SCPStorageEntity> storageEty = scpStorageRepository.findByStorageId(request.getStorageId());
        return storageEty.map(scpStorageEntity -> mapper.map(scpStorageEntity, SCPStorage.newBuilder().getClass()).build());
    }

    @Override
    public SCPStorage createSCPStorage(SCPStorageCreateRequest request) {
        SCPStorageEntity savedEntity = scpStorageRepository.save(mapper.map(request, SCPStorageEntity.class));
        return mapper.map(savedEntity, SCPStorage.newBuilder().getClass()).build();
    }

    @Override
    public boolean updateSCPStorage(SCPStorageUpdateRequest request) {
        SCPStorageEntity updatedEntity = scpStorageRepository.save(mapper.map(request, SCPStorageEntity.class));
        return true;
    }

    @Override
    public boolean deleteSCPStorage(SCPStorageDeleteRequest request) {
        //scpStorageRepository.delete(request.getStorageId());
        return true;
    }

    @Override
    public Optional<SCPResource> getSCPResource(SCPResourceGetRequest request) {
        Optional<SCPResourceEntity> resourceEntity = scpResourceRepository.findByResourceId(request.getResourceId());

        return resourceEntity.map(scpResourceEntity -> mapper.map(scpResourceEntity, SCPResource.newBuilder().getClass())
                .setScpStorage(mapper.map(scpResourceEntity.getScpStorage(), SCPStorage.newBuilder().getClass())).build());
        // Here we have to do nested mapping as the dozer -> protobuf conversion is not happening for inner objects
    }

    @Override
    public SCPResource createSCPResource(SCPResourceCreateRequest request) {
        SCPResourceEntity savedEntity = scpResourceRepository.save(mapper.map(request, SCPResourceEntity.class));
        return getSCPResource(SCPResourceGetRequest.newBuilder().setResourceId(savedEntity.getResourceId()).build()).get();
    }

    @Override
    public boolean updateSCPResource(SCPResourceUpdateRequest request) {
        SCPResourceEntity updatedEntity = scpResourceRepository.save(mapper.map(request, SCPResourceEntity.class));
        return true;
    }

    @Override
    public boolean deleteSCPResource(SCPResourceDeleteRequest request) {
        scpResourceRepository.deleteById(request.getResourceId());
        return true;
    }

    @Override
    public Optional<LocalStorage> getLocalStorage(LocalStorageGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public LocalStorage createLocalStorage(LocalStorageCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateLocalStorage(LocalStorageUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteLocalStorage(LocalStorageDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<LocalResource> getLocalResource(LocalResourceGetRequest request) {
        Optional<LocalResourceEntity> resourceEntity = localResourceRepository.findByResourceId(request.getResourceId());
        return resourceEntity.map(scpResourceEntity -> mapper.map(scpResourceEntity, LocalResource.newBuilder().getClass()).build());
    }

    @Override
    public LocalResource createLocalResource(LocalResourceCreateRequest request) {
        LocalResourceEntity savedEntity = localResourceRepository.save(mapper.map(request, LocalResourceEntity.class));
        return mapper.map(savedEntity, LocalResource.newBuilder().getClass()).build();
    }

    @Override
    public boolean updateLocalResource(LocalResourceUpdateRequest request) {
        LocalResourceEntity updatedEntity = localResourceRepository.save(mapper.map(request, LocalResourceEntity.class));
        return true;
    }

    @Override
    public boolean deleteLocalResource(LocalResourceDeleteRequest request) {
        localResourceRepository.deleteById(request.getResourceId());
        return true;
    }

    @Override
    public Optional<S3Storage> getS3Storage(S3StorageGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public S3Storage createS3Storage(S3StorageCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateS3Storage(S3StorageUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteS3Storage(S3StorageDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<S3Resource> getS3Resource(S3ResourceGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");

    }

    @Override
    public S3Resource createS3Resource(S3ResourceCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");

    }

    @Override
    public boolean updateS3Resource(S3ResourceUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");

    }

    @Override
    public boolean deleteS3Resource(S3ResourceDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");

    }

    @Override
    public Optional<BoxStorage> getBoxStorage(BoxStorageGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public BoxStorage createBoxStorage(BoxStorageCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateBoxStorage(BoxStorageUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteBoxStorage(BoxStorageDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<BoxResource> getBoxResource(BoxResourceGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public BoxResource createBoxResource(BoxResourceCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateBoxResource(BoxResourceUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteBoxResource(BoxResourceDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<AzureStorage> getAzureStorage(AzureStorageGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public AzureStorage createAzureStorage(AzureStorageCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateAzureStorage(AzureStorageUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteAzureStorage(AzureStorageDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<AzureResource> getAzureResource(AzureResourceGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public AzureResource createAzureResource(AzureResourceCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateAzureResource(AzureResourceUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteAzureResource(AzureResourceDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<GCSStorage> getGCSStorage(GCSStorageGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public GCSStorage createGCSStorage(GCSStorageCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateGCSStorage(GCSStorageUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteGCSStorage(GCSStorageDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<GCSResource> getGCSResource(GCSResourceGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public GCSResource createGCSResource(GCSResourceCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateGCSResource(GCSResourceUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteGCSResource(GCSResourceDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<DropboxStorage> getDropboxStorage(DropboxStorageGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public DropboxStorage createDropboxStorage(DropboxStorageCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateDropboxStorage(DropboxStorageUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteDropboxStorage(DropboxStorageDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<DropboxResource> getDropboxResource(DropboxResourceGetRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public DropboxResource createDropboxResource(DropboxResourceCreateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean updateDropboxResource(DropboxResourceUpdateRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public boolean deleteDropboxResource(DropboxResourceDeleteRequest request) throws Exception {
        throw new UnsupportedOperationException("Operation is not supported in backend");
    }

    @Override
    public Optional<FTPResource> getFTPResource(FTPResourceGetRequest request) {
        Optional<FTPResourceEntity> resourceEntity = ftpResourceRepository.findByResourceId(request.getResourceId());

        return resourceEntity.map(ftpResourceEntity -> mapper.map(ftpResourceEntity, FTPResource.newBuilder().getClass())
                .setFtpStorage(mapper.map(ftpResourceEntity.getFtpStorage(), FTPStorage.newBuilder().getClass())).build());
    }

    @Override
    public FTPResource createFTPResource(FTPResourceCreateRequest request) {
        FTPResourceEntity savedEntity = ftpResourceRepository.save(mapper.map(request, FTPResourceEntity.class));
        return getFTPResource(FTPResourceGetRequest.newBuilder().setResourceId(savedEntity.getResourceId()).build()).orElse(null);
    }

    @Override
    public boolean updateFTPResource(FTPResourceUpdateRequest request) {
        ftpResourceRepository.save(mapper.map(request, FTPResourceEntity.class));
        return true;
    }

    @Override
    public boolean deleteFTPResource(FTPResourceDeleteRequest request) {
        ftpResourceRepository.deleteById(request.getResourceId());
        return true;
    }

    @Override
    public Optional<FTPStorage> getFTPStorage(FTPStorageGetRequest request) {
        Optional<FTPStorageEntity> storageEty = ftpStorageRepository.findByStorageId(request.getStorageId());
        return storageEty.map(ftpStorageEntity -> mapper.map(ftpStorageEntity, FTPStorage.newBuilder().getClass()).build());
    }

    @Override
    public FTPStorage createFTPStorage(FTPStorageCreateRequest request) {
        FTPStorageEntity savedEntity = ftpStorageRepository.save(mapper.map(request, FTPStorageEntity.class));
        return mapper.map(savedEntity, FTPStorage.newBuilder().getClass()).build();
    }

    @Override
    public boolean updateFTPStorage(FTPStorageUpdateRequest request) {
        ftpStorageRepository.save(mapper.map(request, FTPStorageEntity.class));
        return true;
    }

    @Override
    public boolean deleteFTPStorage(FTPStorageDeleteRequest request) {
        ftpResourceRepository.deleteById(request.getStorageId());
        return true;
    }

}
