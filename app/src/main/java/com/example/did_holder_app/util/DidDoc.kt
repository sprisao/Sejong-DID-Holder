package com.example.did_holder_app.util

import java.util.*

//object DidDoc {
//    var mContext: Context? = null
//    fun DidDocumentCt(context: Context?, sChallenge: String?): String {
//        mContext = context
//        var sDidDoc = ""
//        val data: ArrayList<ResultData> = ArrayList<ResultData>()
//        try {
//            //
//
//            // 1. Generate Asymmetric Key
//
//            //
//
//            // 예제에서는 사용자용 공개키로 EdDSA, RSA 알고리즘 키를 각 1개씩 생성.
//
//            // - 사용자의 공개키쌍 생성 (EdDSA 알고리즘)
//            val keySetEdDSA: AsymmKeySet = KeySetGeneratorEdDSA.generateEdDSA()
//
//            // - 사용자의 공개키쌍 생성 (RSA 2048 알고리즘)
//            //AsymmKeySet keySetRSA   = KeySetGeneratorRSA.generateRSA(2048);
//            val bArrayPrivateKey: ByteArray = keySetEdDSA.getPrivateKeyEncoded()
//            val bArrayPublicKey: ByteArray = keySetEdDSA.getPublicKeyEncoded()
//            val sPassword: String = MmLib.getParam(mContext, "userNewPWD")
//            if (!Constant.SECURITY_VULNERABILITIES) HHLogs.d("DidDocumentCt sPassword : $sPassword")
//            val encryptedPriKey: ByteArray
//            val priKey: ByteArray
//            try {
//                encryptedPriKey =
//                    PKCS8Encryptor.encrypt(keySetEdDSA.getPrivateKey(), sPassword)
//                writeFile(
//                    context,
//                    MmConfig.SAVE_KEY_PATH,
//                    MmConfig.SAVE_KEY_PATH2,
//                    encryptedPriKey
//                )
//                priKey = PKCS8Encryptor.decryptToEncodedKey(encryptedPriKey, sPassword)
//                if (Arrays.equals(bArrayPrivateKey, priKey)) {
//                    HHLogs.d("DidDocumentCt same 0")
//                }
//                var sVal: String = Base64.encodeToString(encryptedPriKey, Base64.DEFAULT)
//                HHLogs.d("DidDocumentCt encryptedPriKey : $sVal")
//                //MmLib.setParam(mContext,"encryptedPriKey", sVal);
//                sVal = Base64.encodeToString(bArrayPublicKey, Base64.DEFAULT)
//                HHLogs.d("DidDocumentCt bArrayPublicKey : $sVal")
//                MmLib.setParam(mContext, "pubKey", sVal)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } catch (e: PKCSException) {
//                e.printStackTrace()
//            } catch (e: InvalidKeySpecException) {
//                e.printStackTrace()
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//            } catch (e: OperatorCreationException) {
//                e.printStackTrace()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            // repairPrivateKey(sMnemonicCode);
//            //
//            // 2. Generate DID
//            //
//
//            // - DID의 method specific id의 생성 방법은 난수 생성을 방법을 활용.
//            // - 공개키의 일부 바이트 값을 사용하는 방법도 있음
//            // DID did = DIDGenerator.generate(DidRegistryInfo.method, DidRegistryInfo.methodSpecificIdLength);
//            val sDid: String = MmLib.generateDid(bArrayPublicKey)
//            //
//            // 3. Generate DidDocument
//            //
//            val didDoc = DidDocument(sDid)
//
//            // - DID 규격에서 지정한 https://www.w3.org/ns/did/v1 이 DidDocument 내에 기본 context로 저징되어 있음.
//            // - 추가 context가 필요할 경우 호출
//            // didDoc.addContext();
//
//            // 3-1. 생성한 공개키쌍을 DID publicKey 형식으로 변환.
//            val didPubKey = PublicKeyEd25519Signature2018()
//            didPubKey.setId(didDoc.getPublicKeyIdCandidate("keys-"))
//            // 3-1-3. publicKey의 controller 지정. 일반적으로 DID의 소유자.
//            didPubKey.setController(didDoc.getDid())
//            // 3-1-4. byte[] 타입의 publicKey 설정.
//            didPubKey.setPublicKeyEncoded(keySetEdDSA.getPublicKeyEncoded())
//
//            // 3-2. DIDDocument 에 publicKey 지정.
//            didDoc.addPublicKey(didPubKey)
//
//            // 3-3. authentication 정보 지정.
//            didDoc.addAuthentication(didPubKey.getId())
//
//            // 3-5. DidDocument 생성일 지정 (optional)
//            didDoc.setCreated(Date())
//
//            // 3-6. DidDocument 업데이트일 지정 (optional)
//            didDoc.setUpdated(Date())
//            data.add(
//                ResultData(
//                    0,
//                    "[DID Document with 1 public key] :: " + didDoc.serialize()
//                )
//            )
//
//            // - EdDSA 키로 전자서명할 경우.
//            // [참고] 2020.06.16 W3C DID 규격에서 DidDocument의 proof 관련 내용이 삭제됨.
//            val proof = Proof()
//            // 4-1. EdDSA 서명 타입 지정.
//            proof.setType(didPubKey.getType())
//
//            // 4-2. 20바이트의 챌린지값 지정 (일반적으로 DiD Document를 수신해서 검증할 곳으로부터 전달 받음.
//            proof.setChallenge(sChallenge)
//
//            // 4-3. 서명 생성 시각 지정.
//            proof.setCreated(Date())
//
//            // 4-4. 서명 생성 목적 지정.
//            proof.setProofPurpose(ProofPurpose.AUTHENTICATION)
//
//            // 4-5. 서명을 요청한 도메인 정보.
//            proof.setDomain(DidRegistryInfo.domain)
//
//            // 4-6. 서명한 publicKey id 지정.
//            proof.setVerificationMethod(didPubKey.getId())
//
//            // 4-7. 서명 생성.
//            didDoc.sign(proof, keySetEdDSA.getPrivateKeyEncoded())
//            sDidDoc = didDoc.serialize()
//            val verifydidDoc = DidDocument()
//            verifydidDoc.deserialize(sDidDoc)
//            data.add(
//                ResultData(
//                    0,
//                    "[DID Document with Signature] :: " + didDoc.serialize()
//                )
//            )
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//            data.add(
//                ResultData(
//                    1,
//                    "[NoSuchAlgorithmException or InvalidKeySpecException] :: " + e.getStackTrace()
//                )
//            )
//        } catch (e: InvalidKeySpecException) {
//            e.printStackTrace()
//            data.add(
//                ResultData(
//                    1,
//                    "[NoSuchAlgorithmException or InvalidKeySpecException] :: " + e.getStackTrace()
//                )
//            )
//        } catch (e: UnsupportedEncodingException) {
//            e.printStackTrace()
//            data.add(
//                ResultData(
//                    1,
//                    "[UnsupportedEncodingException] :: " + e.getStackTrace()
//                )
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//            data.add(ResultData(1, "[Exception] :: " + e.stackTrace))
//        } finally {
//            // sValue = sValue.replace("\t","\\t" ).replace("\n","\\n" ).replace("\f","\\f" ).replace("\r","\\r" );
//            return sDidDoc
//        }
//    }
//
//    @Throws(Exception::class)
//    fun createVcPortalMessage(
//        context: Context?,
//        challenge: String?
//    ): String {
//        mContext = context
//        val holderDidDoc = DidDocument()
//        val sdidDoc: String = MmLib.getParam(mContext, "userIdDoc")
//        holderDidDoc.deserialize(sdidDoc)
//        val sDid: String = holderDidDoc.getDid()
//        val `object` = JSONObject()
//        if (sDid.length == 0) return ""
//        `object`.put("key", sDid)
//        val sReq: String = `object`.toString()
//        val map: Map<String, Any> = HashMap()
//        val mapper = ObjectMapper()
//        val msg = MsgPortalIdVCReq()
//        val Name: String = MmLib.getParam(mContext, "userName")
//        val BirthDate: String = MmLib.getParam(mContext, "userBirthDate")
//        val Number: String = MmLib.getParam(mContext, "userNumber")
//        msg.setBirthDate(BirthDate)
//        msg.setUUID(Number)
//        msg.setName(Name)
//        msg.setTid(generateMethodSpecificId(32))
//
//        // 사용자의 공개키 획득.
//        val pubKeys: ArrayList<DidPublicKeyPrimitive> =
//            holderDidDoc.getPublicKeySet()
//        val pubKeyId: String = pubKeys[0].getId()
//        val pubKeyType: String = pubKeys[0].getType()
//        val proof = Proof()
//        proof.setType(pubKeyType)
//        proof.setCreated(Date())
//        proof.setChallenge(challenge) // VCA가 세션 유지 능력이 없기 때문에, 현재는 클라이언트에서
//        // challenge 생성.
//
//        // "/challenge" 에서 받은 첼린지값을 가져와 Proof 생성 . Sample 이라 직접 만들고 검증시 체크하지않음.
//        proof.setVerificationMethod(pubKeyId)
//        proof.setProofPurpose(ProofPurpose.ASSERTION_METHOD)
//        try {
//            val bytesPrkey1 = privateKey
//            msg.sign(proof, bytesPrkey1)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return msg.serialize()
//    }
//
//    @Throws(Exception::class)
//    fun createVpPortalMessage(context: Context?, challenge: String): String {
//        mContext = context
//        val didDoc = DidDocument()
//        val sdidDoc: String = MmLib.getParam(mContext, "userIdDoc")
//        try {
//            didDoc.deserialize(sdidDoc)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } catch (e: InstantiationException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }
//        val sVC: String = MmLib.getParam(mContext, "userVC")
//        if (!Constant.SECURITY_VULNERABILITIES) HHLogs.d("didVpCreate sVC : $sVC")
//        if (sVC.isEmpty()) {
//            return ""
//        }
//        val portalVC = NhisPortalIdVc()
//        try {
//            // 더 건강보험 일경우 portal VC
//            portalVC.deserialize(sVC)
//        } catch (e: InstantiationException) {
//            // fail to parse portal VC.
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        //각각의 VC를 받급받으면 됨
//        val prikey = privateKey
//        val vp = VerifiablePresentation()
//        val sDid: String = didDoc.getDid()
//        val vpId: String = generateMethodSpecificId(32)
//        vp.setId(vpId) // OPTIONAL : uuid, url 등 unique 한 id를 생성해서 설정
//        vp.setHolder(sDid)
//        vp.addVerifiableCredentials(portalVC)
//        val pubKeys: ArrayList<DidPublicKeyPrimitive> = didDoc.getPublicKeySet()
//        val pubKeyId: String = pubKeys[0].getId()
//        val pubKeyType: String = pubKeys[0].getType()
//
//        // Proof 생성.
//        val proof = Proof()
//
//        // Proof type
//        proof.setType(pubKeyType)
//        // Proof 생성 시각
//        proof.setCreated(Date())
//        // Proof challenge
//        proof.setChallenge(challenge)
//
//        // Proof 생성에 사용된 공개키 정보
//        proof.setVerificationMethod(pubKeyId)
//
//        // Proof 생성 목적
//        proof.setProofPurpose(ProofPurpose.AUTHENTICATION)
//
//        // VP 도메인 정보
//        proof.setDomain(DidRegistryInfo.domain)
//
//        // Proof 생성
//        vp.sign(proof, prikey)
//        val sVp: String = vp.serialize()
//        val receivedVP: VerifiablePresentation = vp
//        // IDServer 세션의 challenge와 비교
//        if (receivedVP.getProof().getChallenge().equals(challenge)) {
//            // 세션의 challenge와 VP내의 challenge가 일치하면,
//            // "세션의 challenge 삭제하세요." : 재사용 방지 목적입니다.
//            HHLogs.d("4. VP 내의 challenge 검사 : 성공")
//            HHLogs.d("	receivedChallenge : $challenge")
//            HHLogs.d("	 challengeInProof : " + receivedVP.getProof().getChallenge())
//        } else {
//            // IDServer 세션의 challenge와 비교하여 일치하지 않으면 reject.
//            HHLogs.d("4. VP 내의 challenge 검사 : 실패")
//            HHLogs.d("	receivedChallenge : $challenge")
//            HHLogs.d("	 challengeInProof : " + receivedVP.getProof().getChallenge())
//            // 사용자 요청처리 중단
//        }
//        return sVp
//    }
//
//    val privateKey: ByteArray
//        get() {
//            val sPassword: String = MmLib.getParam(mContext, "userNewPWD")
//            if (!Constant.SECURITY_VULNERABILITIES) HHLogs.d("getPrivateKey sPassword : $sPassword")
//
//            //String sEncryptedPriKey = MmLib.getParam(mContext,"encryptedPriKey");
//            //HHLogs.d("getPrivateKey sEncryptedPriKey : " + sEncryptedPriKey);
//            //byte[]  encryptedPriKey = Base64.decode(sEncryptedPriKey, Base64.DEFAULT);
//            var priKey = ByteArray(0)
//            try {
//                val encKeyFromStorage: ByteArray =
//                    readFile(mContext, MmConfig.SAVE_KEY_PATH + MmConfig.SAVE_KEY_PATH2)
//                try {
//                    priKey =
//                        PKCS8Encryptor.decryptToEncodedKey(encKeyFromStorage, sPassword)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                } catch (e: PKCSException) {
//                    e.printStackTrace()
//                } catch (e: InvalidKeySpecException) {
//                    e.printStackTrace()
//                } catch (e: NoSuchAlgorithmException) {
//                    e.printStackTrace()
//                } catch (e: OperatorCreationException) {
//                    e.printStackTrace()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return priKey
//        }
//
//    fun verifyPW(context: Context?, sPassword: String?): Boolean {
//        var bRet = false
//        var priKey = ByteArray(0)
//        try {
//            val encKeyFromStorage: ByteArray =
//                readFile(context, MmConfig.SAVE_KEY_PATH + MmConfig.SAVE_KEY_PATH2)
//            try {
//                priKey = PKCS8Encryptor.decryptToEncodedKey(encKeyFromStorage, sPassword)
//                bRet = true
//            } catch (e: IOException) {
//                e.printStackTrace()
//                bRet = false
//            } catch (e: PKCSException) {
//                e.printStackTrace()
//                bRet = false
//            } catch (e: InvalidKeySpecException) {
//                e.printStackTrace()
//                bRet = false
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//                bRet = false
//            } catch (e: OperatorCreationException) {
//                e.printStackTrace()
//                bRet = false
//            } catch (e: Exception) {
//                e.printStackTrace()
//                bRet = false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            bRet = false
//        }
//        return bRet
//    }
//
//    fun changePW(context: Context?, sOldPW: String?, sNewPW: String?): Boolean {
//        var bRet = false
//        val priKey = ByteArray(0)
//        bRet = try {
//            val encKeyFromStorage: ByteArray =
//                readFile(context, MmConfig.SAVE_KEY_PATH + MmConfig.SAVE_KEY_PATH2)
//            try {
//                val privateKey: PrivateKey =
//                    PKCS8Encryptor.decrypt(encKeyFromStorage, sOldPW)
//                val encryptedKey: ByteArray = PKCS8Encryptor.encrypt(privateKey, sNewPW)
//                writeFile(
//                    context,
//                    MmConfig.SAVE_KEY_PATH,
//                    MmConfig.SAVE_KEY_PATH2,
//                    encryptedKey
//                )
//                true
//            } catch (e: IOException) {
//                e.printStackTrace()
//                false
//            } catch (e: PKCSException) {
//                e.printStackTrace()
//                false
//            } catch (e: InvalidKeySpecException) {
//                e.printStackTrace()
//                false
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//                false
//            } catch (e: OperatorCreationException) {
//                e.printStackTrace()
//                false
//            } catch (e: Exception) {
//                e.printStackTrace()
//                false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//        return bRet
//    }
//}
