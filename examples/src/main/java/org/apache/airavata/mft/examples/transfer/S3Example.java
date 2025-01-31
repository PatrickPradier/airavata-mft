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

package org.apache.airavata.mft.examples.transfer;

import org.apache.airavata.mft.api.client.MFTApiClient;
import org.apache.airavata.mft.api.service.*;
import org.apache.airavata.mft.common.AuthToken;
import org.apache.airavata.mft.common.UserTokenAuth;

import java.util.Iterator;

public class S3Example {
    public static void main(String args[]) throws Exception {
        MFTTransferServiceGrpc.MFTTransferServiceBlockingStub client = MFTApiClient.MFTApiClientBuilder
                .newBuilder().build().getTransferClient();

        String sourceResourceId = "remote-ssh-storage";
        String sourceResourcePath = "/tmp/1mb.txt";
        String sourceToken = "ssh-cred";
        String destResourceId = "s3-storage-1";
        String destResourcePath = "1mb-copy.txt";
        String destToken = "s3-cred";
        AuthToken mftAuthorizationToken = AuthToken.newBuilder().setUserTokenAuth(UserTokenAuth.newBuilder().setToken("43ff79ac-e4f2-473c-9ea1-04eee9509a53").build()).build();

        TransferApiRequest request = TransferApiRequest.newBuilder()
                .setMftAuthorizationToken(mftAuthorizationToken)
                .setSourceResourceId(sourceResourceId)
                .setSourceToken(sourceToken)
                .setSourceType("SCP")
                .setDestinationResourceId(destResourceId)
                .setDestinationToken(destToken)
                .setDestinationType("S3")
                .setAffinityTransfer(false).build();

        TransferApiResponse transferApiResponse = client.submitTransfer(request);
        while(true) {

            try {
                Iterator<TransferStateApiResponse> transferStates = client.getTransferStates(TransferStateApiRequest.newBuilder().setTransferId(transferApiResponse.getTransferId()).build());
                System.out.println("Got " + transferStates.next().getState());
                TransferStateApiResponse transferState = client.getTransferState(TransferStateApiRequest.newBuilder().setTransferId(transferApiResponse.getTransferId()).build());
                System.out.println("State " + transferState.getState());
                if ("COMPLETED".equals(transferState.getState()) || "FAILED".equals(transferState.getState())) {
                    break;
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            Thread.sleep(1000);
        }
    }
}
