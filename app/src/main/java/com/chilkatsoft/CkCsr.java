/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkCsr {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected CkCsr(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkCsr obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkCsr(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CkCsr() {
    this(chilkatJNI.new_CkCsr(), true);
  }

  public void LastErrorXml(CkString str) {
    chilkatJNI.CkCsr_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorHtml(CkString str) {
    chilkatJNI.CkCsr_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorText(CkString str) {
    chilkatJNI.CkCsr_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void get_CommonName(CkString str) {
    chilkatJNI.CkCsr_get_CommonName(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String commonName() {
    return chilkatJNI.CkCsr_commonName(swigCPtr, this);
  }

  public void put_CommonName(String newVal) {
    chilkatJNI.CkCsr_put_CommonName(swigCPtr, this, newVal);
  }

  public void get_Company(CkString str) {
    chilkatJNI.CkCsr_get_Company(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String company() {
    return chilkatJNI.CkCsr_company(swigCPtr, this);
  }

  public void put_Company(String newVal) {
    chilkatJNI.CkCsr_put_Company(swigCPtr, this, newVal);
  }

  public void get_CompanyDivision(CkString str) {
    chilkatJNI.CkCsr_get_CompanyDivision(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String companyDivision() {
    return chilkatJNI.CkCsr_companyDivision(swigCPtr, this);
  }

  public void put_CompanyDivision(String newVal) {
    chilkatJNI.CkCsr_put_CompanyDivision(swigCPtr, this, newVal);
  }

  public void get_Country(CkString str) {
    chilkatJNI.CkCsr_get_Country(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String country() {
    return chilkatJNI.CkCsr_country(swigCPtr, this);
  }

  public void put_Country(String newVal) {
    chilkatJNI.CkCsr_put_Country(swigCPtr, this, newVal);
  }

  public void get_DebugLogFilePath(CkString str) {
    chilkatJNI.CkCsr_get_DebugLogFilePath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String debugLogFilePath() {
    return chilkatJNI.CkCsr_debugLogFilePath(swigCPtr, this);
  }

  public void put_DebugLogFilePath(String newVal) {
    chilkatJNI.CkCsr_put_DebugLogFilePath(swigCPtr, this, newVal);
  }

  public void get_EmailAddress(CkString str) {
    chilkatJNI.CkCsr_get_EmailAddress(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String emailAddress() {
    return chilkatJNI.CkCsr_emailAddress(swigCPtr, this);
  }

  public void put_EmailAddress(String newVal) {
    chilkatJNI.CkCsr_put_EmailAddress(swigCPtr, this, newVal);
  }

  public void get_HashAlgorithm(CkString str) {
    chilkatJNI.CkCsr_get_HashAlgorithm(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String hashAlgorithm() {
    return chilkatJNI.CkCsr_hashAlgorithm(swigCPtr, this);
  }

  public void put_HashAlgorithm(String newVal) {
    chilkatJNI.CkCsr_put_HashAlgorithm(swigCPtr, this, newVal);
  }

  public void get_LastErrorHtml(CkString str) {
    chilkatJNI.CkCsr_get_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorHtml() {
    return chilkatJNI.CkCsr_lastErrorHtml(swigCPtr, this);
  }

  public void get_LastErrorText(CkString str) {
    chilkatJNI.CkCsr_get_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorText() {
    return chilkatJNI.CkCsr_lastErrorText(swigCPtr, this);
  }

  public void get_LastErrorXml(CkString str) {
    chilkatJNI.CkCsr_get_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorXml() {
    return chilkatJNI.CkCsr_lastErrorXml(swigCPtr, this);
  }

  public boolean get_LastMethodSuccess() {
    return chilkatJNI.CkCsr_get_LastMethodSuccess(swigCPtr, this);
  }

  public void put_LastMethodSuccess(boolean newVal) {
    chilkatJNI.CkCsr_put_LastMethodSuccess(swigCPtr, this, newVal);
  }

  public void get_Locality(CkString str) {
    chilkatJNI.CkCsr_get_Locality(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String locality() {
    return chilkatJNI.CkCsr_locality(swigCPtr, this);
  }

  public void put_Locality(String newVal) {
    chilkatJNI.CkCsr_put_Locality(swigCPtr, this, newVal);
  }

  public void get_MgfHashAlg(CkString str) {
    chilkatJNI.CkCsr_get_MgfHashAlg(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String mgfHashAlg() {
    return chilkatJNI.CkCsr_mgfHashAlg(swigCPtr, this);
  }

  public void put_MgfHashAlg(String newVal) {
    chilkatJNI.CkCsr_put_MgfHashAlg(swigCPtr, this, newVal);
  }

  public boolean get_PssPadding() {
    return chilkatJNI.CkCsr_get_PssPadding(swigCPtr, this);
  }

  public void put_PssPadding(boolean newVal) {
    chilkatJNI.CkCsr_put_PssPadding(swigCPtr, this, newVal);
  }

  public void get_State(CkString str) {
    chilkatJNI.CkCsr_get_State(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String state() {
    return chilkatJNI.CkCsr_state(swigCPtr, this);
  }

  public void put_State(String newVal) {
    chilkatJNI.CkCsr_put_State(swigCPtr, this, newVal);
  }

  public boolean get_VerboseLogging() {
    return chilkatJNI.CkCsr_get_VerboseLogging(swigCPtr, this);
  }

  public void put_VerboseLogging(boolean newVal) {
    chilkatJNI.CkCsr_put_VerboseLogging(swigCPtr, this, newVal);
  }

  public void get_Version(CkString str) {
    chilkatJNI.CkCsr_get_Version(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String version() {
    return chilkatJNI.CkCsr_version(swigCPtr, this);
  }

  public boolean AddSan(String sanType, String sanValue) {
    return chilkatJNI.CkCsr_AddSan(swigCPtr, this, sanType, sanValue);
  }

  public boolean GenCsrBd(CkPrivateKey privKey, CkBinData csrData) {
    return chilkatJNI.CkCsr_GenCsrBd(swigCPtr, this, CkPrivateKey.getCPtr(privKey), privKey, CkBinData.getCPtr(csrData), csrData);
  }

  public boolean GenCsrPem(CkPrivateKey privKey, CkString outStr) {
    return chilkatJNI.CkCsr_GenCsrPem(swigCPtr, this, CkPrivateKey.getCPtr(privKey), privKey, CkString.getCPtr(outStr), outStr);
  }

  public String genCsrPem(CkPrivateKey privKey) {
    return chilkatJNI.CkCsr_genCsrPem(swigCPtr, this, CkPrivateKey.getCPtr(privKey), privKey);
  }

  public boolean GetExtensionRequest(CkXml extensionReqXml) {
    return chilkatJNI.CkCsr_GetExtensionRequest(swigCPtr, this, CkXml.getCPtr(extensionReqXml), extensionReqXml);
  }

  public boolean GetPublicKey(CkPublicKey pubkey) {
    return chilkatJNI.CkCsr_GetPublicKey(swigCPtr, this, CkPublicKey.getCPtr(pubkey), pubkey);
  }

  public boolean GetSans(CkStringTable sans) {
    return chilkatJNI.CkCsr_GetSans(swigCPtr, this, CkStringTable.getCPtr(sans), sans);
  }

  public boolean GetSubjectField(String oid, CkString outStr) {
    return chilkatJNI.CkCsr_GetSubjectField(swigCPtr, this, oid, CkString.getCPtr(outStr), outStr);
  }

  public String getSubjectField(String oid) {
    return chilkatJNI.CkCsr_getSubjectField(swigCPtr, this, oid);
  }

  public String subjectField(String oid) {
    return chilkatJNI.CkCsr_subjectField(swigCPtr, this, oid);
  }

  public boolean LoadCsrPem(String csrPemStr) {
    return chilkatJNI.CkCsr_LoadCsrPem(swigCPtr, this, csrPemStr);
  }

  public boolean SaveLastError(String path) {
    return chilkatJNI.CkCsr_SaveLastError(swigCPtr, this, path);
  }

  public boolean SetExtensionRequest(CkXml extensionReqXml) {
    return chilkatJNI.CkCsr_SetExtensionRequest(swigCPtr, this, CkXml.getCPtr(extensionReqXml), extensionReqXml);
  }

  public boolean SetSubjectField(String oid, String value, String asnType) {
    return chilkatJNI.CkCsr_SetSubjectField(swigCPtr, this, oid, value, asnType);
  }

  public boolean VerifyCsr() {
    return chilkatJNI.CkCsr_VerifyCsr(swigCPtr, this);
  }

}
