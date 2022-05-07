/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import deLocale from 'element-plus/lib/locale/lang/de';

const de = {
  [deLocale.name]: {
    el: deLocale.el,
    about: {
      'line-2': 'Über uns',
      'line-3': 'Visual management tool für IoTDB',
      'line-4': `IoTDB Admin ist eine browserbasiertes Verwaltungsoberfläche für IoTDB, dass alle nötigen Operationen wie das Hinzufügen, Löschen, Verändern oder Abfragen von Werten ermöglicht. Ausserdem wird die Zugriffskontrolle unterstützt. Es vereinfacht die Benutzung von IoTDB deutlich und ist einfach zu verwenden.`,
      'line-5': `IoTDB ist unserer Meinung nach eine der besten Zeitreihendatenbanken. Wir werden stets versuchen die Entwicklung und Anwendung dieser Zeitreihendatenbank zu unterstützen. Wir heissen jeden herzlich Willkommen mitzumachen. Ihr könnt uns kontaktieren unter:`,
      'line-6-text': 'Wechat scannen',
      'line-7': 'Version: 0.12',
      'back-btn': 'Zurück zur Hauptseite',
    },
    common: {
      submit: 'Absenden',
      cancel: 'Abbrechen',
      detail: 'Details',
      delete: 'Löschen',
      edit: 'Bearbeiten',
      operation: 'Oeration',
      save: 'Speichern',
      clear: 'Leer',
      noData: 'Keine Daten',
      tip: 'Tips',
      deleteSuccess: 'Löschen erfolgreich',
      add: 'neu hinzugefügt',
      placeHolder: 'Bitte geben Sie ein',
      selectPlaceholder: 'Wählen Sie',
      survival: 'Überleben',
      death: 'Tod',
      port: 'Hafen',
      refresh: 'Auffrischen',
      startTime: 'Startzeit',
      endTime: 'Endzeit',
      all: 'Alle',
      success: 'Erfolgreich',
      fail: 'Fehlgeschlagen',
      query: 'Abfragen',
    },
    databasem: {
      newStoreGroup: 'Neue Speichergruppe ("storage group")',
      query: 'Query',
      newDevice: 'Neues Device',
      newQuery: 'Neues Query',
    },
    rootPage: {
      chinalang: 'Chinesisch',
      englishlang: 'Englisch',
      deutsch: 'Deutsch',
      loginoutText: 'Ausloggen',
      about: 'Über uns',
      help: 'Hilfe',
      databaseManagement: 'Datenbankmanagement',
      dataList: 'Datenliste',
      newdatasource: 'Datenquelle',
      newQueryWindow: 'Query',
      nodatasource: 'Aktuell existiert keine Datenquelle',
      feedback: 'Problem Feedback',
      monitorManagement: 'Überwachungsverwaltung',
    },
    loginPage: {
      account: 'Account',
      password: 'Password',
      placeholderAccount: 'Benutzernamen eingeben',
      placeholderPassword: 'Passwort eingeben',
      forgetPassWord: 'Passwort vergessen',
      signIn: 'Anmelden',
      forgetPassword: 'Passwort vergessen',
      forgetPasswordTip: 'Bitte kontaktieren Sie Ihren Administrator!',
      accountEmptyTip: 'Benutzername darf nicht leer sein',
      accountContentTip: 'Der Benutzername muss aus mindestens 3 Buchstaben, Zahlen oder Unterstrichen bestehen und muss mit einem Buchstaben beginnen',
      accountLengthTip: 'Der Benutzername muss zwischen 3 und 32 Zeichen lang sein',
      passwordEmptyTip: 'Passwort darf nicht leer sein',
      passwordLenghtTip: 'Das Passwort muss mindestens 6 Zeichen lang sein',
      welcomeLogin: 'Willkommen bei der IoTDB Admin Oberfläche',
      loginErrorTip: 'Benutzername oder Passwort falsch',
    },

    sourcePage: {
      alias: 'Verbindung',
      host: 'Host',
      port: 'Port',
      entityNum: 'Anzahl der Unternehmen',
      physicalNum: 'Körperliche Menge',
      dataNum: 'Daten insgesamt',
      username: 'Benutzername',
      password: 'Passwort',
      addDialogTitle: 'Neue Datenquelle',
      editDialogTitle: 'Datenquelle Bearbeiten',
      eg: 'example:127.0.0.1',
      aliasEmptyTip: 'Verbindung darf nicht leer sein',
      hostEmptyTip: 'Host darf nicht leer sein',
      hostErrorTip: 'Host in ungültigem Format',
      portErrorTip: 'Port in ungültigem Format',
      newUserEmptyTip: 'Benutzername darf nicht leer sein',
      newPasswordTip: 'Passwort darf nicht leer sein',
      newUserErrorTip: 'Bitte gültigen Namen eingeben',
      newUserErrorTip1: 'Der Benutzername muss zwischen 4 und 255 Zeichen lang sein',
      newpasswordErrorTip1: 'Das Passwort muss zwischen 4 und 255 Zeichen lang sein',
      aliasErrorTip: 'Der Verbindungsname muss zwischen 3 und 100 Zeichen lan sein',
      portEmptyTip: 'Port darf nicht leer sein',
      usernameEmptyTip: 'Benutzername darf nicht leer sein',
      passwordEmptyTip: 'Password darf nicht leer sein',
      userAccount: 'Benutzeraccount',
      newAccount: 'Neuer Account',
      baseConfig: 'Basiseinstellungen',
      accountPermit: 'Rechte',
      permitPermission: 'Genehmigung für das Management',
      permitTips: 'Anmerkung: Nach Überprüfung der [View] Berechtigung des Benutzers / Rolle kann die entsprechende Erstellung, Löschung, Änderung und andere Berechtigungen wirksam werden',
      dataModel: 'Modell der Daten',
      userNameTitle: 'Benutzername:',
      passwordTitle: 'Passwort:',
      groupInfo: 'Gruppeninformation',
      groupName: 'Gruppenname',
      description: 'Beschreibung',
      line: 'Zeile',
      path: 'Pfad',
      range: 'Bereich',
      func: 'Funktion',
      selectPermissions: 'Wählen Sie Berechtigungen',
      selectAlias: 'Verbindung',
      selectGroup: 'Gruppe',
      selectDevice: 'Device',
      selectTime: 'Zeitreihe',
      createGroup: 'SET_STORAGE_GROUP',
      createUser: 'CREATE_USER',
      deleteUser: 'DELETE_USER',
      editPassword: 'MODIFY_PASSWORD',
      listUser: 'LIST_USER',
      listRole: 'Funktionen anzeigen',
      deleteRole: 'Rolle löschen',
      createRole: 'Rolle erstellen',
      grantRolePrivilege: 'Geben Sie Rollenberechtigungen',
      revertRolePrivilege: 'Widerruf von Rollenberechtigungen',
      grantPrivilege: 'GRANT_USER_PRIVILEGE',
      revertPrivilege: 'REVOKE_USER_PRIVILEGE',
      createTimeSeries: 'CREATE_TIMESERIES',
      insertTimeSeries: 'INSERT_TIMESERIES',
      readTimeSeries: 'READ_TIMESERIES',
      deleteTimeSeries: 'DELETE_TIMESERIES',
      createTrigger: 'CREATE_TRIGGER',
      uninstallTrigger: 'DROP_TRIGGER',
      startTrigger: 'START_TRIGGER',
      stopTrigger: 'STOP_TRIGGER',
      createFunction: 'CREATE_FUNCTION',
      uninstallFunction: 'DROP_FUNCTION',
      test: 'Verbindungstest',
      testBtnLabel: 'Test',
      testResult: 'Verbindungstest erfolgreich',
      noAuthTip: 'Sie haben nicht die nötigen Rechte für diese Aktion',
      addAuthBtn: 'Recht hinzufügen',
      modifySuccessLabel: 'Passwort erfolgreich geändert',
      addSuccessLabel: 'Benutzer erfolgreich erstellt',
      deleteUserSuccessLabel: 'Benutzer erfolgreich gelöscht',
      addFirstLabel: 'Bitte erstellen Sie erst einen Benutzer',
      deleteAuthLabel: 'Recht erfolgreich gelöscht',
      operateAuthLabel: 'you have operate authorition successful',
      deleteGroupLabel: 'Gruppe erfolgreich gelöscht',
      newGroupSuccessLabel: 'Gruppe erfolgreich gespeichert',
      deleteAuthConfirm: 'Soll dieses Recht sicher gelöscht werden?',
      deleteSourceConfirm: 'Soll diese Datenquelle sicher gelöscht werden?',
      deleteUserConfirm: 'Soll der Benutzer sicher gelöscht werden?',
      deleteRoleConfirm: 'Diese Rolle löschen?',
      newSourceSuccessLabel: 'Datenquelle erfolgreich gespeichert',
      addAuthFirstLabel: 'please do auth add present first',
      authTips: 'Vorsicht: Erst nach der Prüfung [LIST_USER], kann [CREATE_USER]、[DELETE_USER]、[MODIFY_PASSWORD]、[GRANT_USER_PRIVILEGE]、[REVOKE_USER_PRIVILEGE] angewendet werden',
      submitRangeTips: 'Bereich darf nicht leer sein',
      submitTypeTips: 'Typ darf nicht leer sein',
      submitPrivilegesTips: 'Funktion darf nicht leer sein',
      groupNameLabel: 'Gruppe:',
      deviceNameLabel: 'Device:',
      timeNameLabel: 'Zeitreihe:',
      ttlErrorTips: 'ttl muss eine positive Ganzzahl sein',
      maxErrorTips: 'Die Überlebenszeit kann 9007199254740992 nicht überschreiten',
      roleList: 'Liste der Aufgaben',
      addRole: 'Rolle hinzufügen',
      dataManagePrivilege: 'Behörde für das Datenmanagement',
      roleName: 'Name der Rolle',
      roleDescription: 'Beschreibung der Rolle',
      grantUserPrivilege: 'Geben Sie Benutzerberechtigung',
      addUser: 'Benutzer hinzufügen',
      inputRoleNameTip: 'Bitte geben Sie einen Kontonamen mit mehr als vier Ziffern ein',
      inputRoleDescTip: 'Bitte geben Sie die Rollenbeschreibung ein',
      roleNameLengthError: 'Ungenügende Rollenlänge',
      roleNameExistError: 'Die Rolle existiert bereits',
      grantUser: 'Zugelassener Benutzer',
      userList: 'Liste der Benutzer',
      selectedUser: 'Ausgewählte Benutzer',
      showMore: 'Weitere Informationen',
      nextPage: 'nächste Seite',
      prePage: 'vorige Seite',
      successEditPermit: 'Berechtigungsmanagement wurde erfolgreich geändert',
      accountPermitLabel: 'Die Kontobehörde',
      userRelevance: 'Benutzerbezogene Daten',
      roleRelevance: 'Zusammenhang mit der Rolle',
      udf: 'UDF',
      trigger: 'Auslöser',
      addPermission: 'Berechtigung hinzufügen',
      editPermission: 'Bearbeiten von Berechtigungen',
      selectdataconnection: 'Bitte wählen Sie eine Speichergruppe aus',
      selectEntity: 'Bitte wählen Sie ein Entity',
      selectTimeseries: 'Bitte wählen Sie die physikalische Menge',
      selectRange: 'Bitte wählen Sie einen Datenbereich',
      selectPermission: 'Bitte wählen Sie die Berechtigung',
      addRoleTip: 'Bitte schließen Sie die Hinzufügung zuerst aus',
      confirmDelete: 'Löschung bestätigen?',
      roleNameLimit1: 'Der Name kann keine reine Zahl sein',
      roleNameLimit2: 'Name existiert bereits',
      allMeasurement: 'All measurement',
      addPrivilegeSuccess: 'Erfolgreich hinzugefügte Berechtigung',
      editPrivilegeSuccess: 'Bearbeitung erfolgreich',
      editSucceed: 'Bearbeiten erfolgreich',
      createdSucceed: 'Erstellt erfolgreich',
      allStorageGroups: 'Alle Speichergruppen',
      allDevices: 'Alle Einheiten',
    },
    storagePage: {
      alias: 'Verbindung',
      creator: 'Ersteller',
      createTime: 'Erstellt:',
      ttl: 'ttl:',
      description: 'Beschreibung',
      deviceName: 'Devicename:',
      newDevice: 'Neues Device',
      line: 'Zeile',
      measurement: 'physikalische Menge',
      operation: 'Aktion',
      millsSecondLabel: 'Millisekunde',
      secondLabel: 'Sekunde',
      minuteLabel: 'Minute',
      hourLabel: 'Stunde',
      dayLabel: 'Tag',
      weekLabel: 'Woche',
      monthLabel: 'Monat',
      yearLabel: 'Jahr',
      groupName: 'Gruppenname:',
      groupDescription: 'Gruppenbeschreibung:',
      tips: 'Tip: Ist keine TTL angegeben ist diese unendlich',
      ttlErrTips: 'ttl und ttl unit müssen gleichzeitig existieren (?)',
      groupNamePlaceholder: 'Bitte Gruppenname eingeben, z.B. factory.robotA',
      groupNameLengthTips: 'Der Gruppenname muss ziwschen 0 und 255 Zeichen lang sein',
      descriptionLengthTips: 'Die Gruppenbeschreibung muss zwischen 0 und 100 Zeichen lang sein',
      deleteGroupConfirm: 'Soll die Gruppe sicher gelöscht werden?',
      deleteDeviceConfirm: 'Mit dieser Aktion wird das Device unwiderfulich gelöscht. Fortfahren?',
    },
    device: {
      measurement: 'measurement',
      serchPy: 'Name der Metrik eingeben',
      dataconnection: 'Verbindung',
      selectdataconnection: 'Speichergruppe ("Storage group") auswählen',
      selectdataconnections: 'Verbindung auswählen',
      devicename: 'Gerätename',
      description: 'Gerätebeschreibung',
      group: 'Speichergruppe ("storage group")',
      physical: 'Metrik',
      addphysical: 'Metrik hinzufügen',
      delete: 'Löschen',
      creator: 'Ersteller',
      createTime: 'Erstellt',
      newValue: 'Neuer Wert',
      deletecontent1: 'Wollen Sie sicher löschen?',
      deletecontent2: 'Gelöschte Daten können nicht widerhergestellt werden',
      cencel: 'Abbrechen',
      ok: 'Ok',
      physicalname: 'Metriken',
      datatype: 'Datentyp',
      codingmode: 'Kodierung',
      physicaldescr: 'Beschreibung der Metrik',
      action: 'Aktion',
      datatrends: 'Datnetrend',
      datatrend: 'Trend der physischen Daten (?)',
      time: 'Uhrzeit',
      look: 'Aussehen',
      inputdevice: 'Bitte geben Sie den Entity-Level-Pfad ein',
      inputdecr: 'Bitte Gerätebeschreibung angeben',
      pyname: 'Physical quantities dürfen nur aus Buchstaben, Zahlen und Unterstrichen bestehen',
      pynamel: 'Physical quantities dürfen nur aus Buchstaben, Zahlen und Unterstrichen bestehen. Sie dürfen nur eine Länge zwischen 0 und 255 Zeichen haben',
      pynamecopy: 'Name der physical quantity schon vorhanden, bitte ändern',
      deletetitle: 'Erfolgreich gelöscht',
      canceldeletion: 'Löschen abbrechen',
      addpydataa: 'Fügen Sie bis zu 2000 Datenpunkte hinzu',
      tips: 'Tips',
      pleaseinput: 'Name der Metrik eingeben',
      selectdata: 'Es muss ein Datentyp ausgewählt werden',
      savesuccess: 'Erfolgreich gespeichert',
      minphysical: 'Die physical quantity darf nicht 0 sein',
      must: 'Bitte geben Sie den nötigen Inhalt ein',
      newquery: 'Neues Query',
      inputfunction: 'Funktionsnamen eingeben',
      selectp: 'Gerät auswählen',
      deleteSuccess: 'Erfolgreich gelöscht',
      selectdatatype: 'Datentyp auswählen',
      tag: 'Etikett',
      attributes: 'Attribut',
      compressionMode: 'Komprimierungsmodus',
      alias: 'Alias',
      dataNum: 'Gesamtdaten (Stück)',
      editTimeseries: 'Physikalische Menge bearbeiten',
      inputTip: 'Bitte geben Sie ein',
      selectTip: 'Bitte wählen',
      entityStructure: 'Struktur des Organs',
      dataPreview: 'Vorschau der Daten',
      addData: 'Neue Daten',
      exportData: 'Exportieren von Daten',
      randomGeneration: 'Zufällige Erzeugung',
      batchImport: 'Einfuhr der Chargen',
      templateDownload: 'Vorlage herunterladen',
      downloadTemplate: 'Vorlage herunterladen',
      fileUpload: 'Datei hochladen',
      uploadTip: 'Nur CSV-Format-Dateien werden unterstützt',
      uploading: 'Hochladen：',
      editData: 'Daten bearbeiten',
      deleteSingleDataTip: 'Sind Sie sicher, die ausgewählten Daten zu löschen',
      exportSucceeded: 'Export erfolgreich',
      screenPhysical: 'Untersuchung der physischen Menge',
      timeRange: 'Zeitrahmen',
      startTime: 'Zeitpunkt des Beginns',
      endTime: 'Die Endzeit',
      randomData: 'Zufällige Daten',
      randomTip: 'Tipp: Daten mit dem gleichen Zeitstempel werden überschrieben',
      importResult: 'Ergebnisse beim Stapelimport:',
      step: 'Schritt',
      generatedQuantity: 'Erzeugte Menge',
      stepTip: 'Bitte geben Sie die Schrittgröße ein',
      stepErrorTip: 'Bitte geben Sie eine positive Ganzzahl ein',
      generateTip: 'Bitte geben Sie die erzeugte Menge ein',
      generateErrorTip: 'Begrenzt auf eine Million',
      uploadedNum: 'Uploads insgesamt',
      successNum: 'Anzahl der Erfolge',
      failedNum: 'Anzahl der Ausfälle',
      deleteTip: 'Bitte zuerst Daten auswählen',
      all: 'ganze',
      chooseDate: 'Zeit auswählen',
      startDateTip: 'Bitte wählen Sie eine Startzeit',
    },
    sqlserch: {
      Aggregate: 'Aggregate',
      math: 'Math',
      string: 'String',
      select: 'Auswählen',
      sum: 'Trend Berechnung',
      date: 'Datum',
      count: 'Anzahl der Datenpunkte',
      avg: 'Gemittelter Wert der Datenpunkte',
      sum1: 'Summe der Datenpunkte',
      fristvalue: 'Erster gespeicherter Wert',
      lastvalue: 'Letzter gespeicherter Wert',
      minvalue: 'Kleinerster Wert',
      maxvalue: 'Größter Wert',
      mintime: 'Kleinster Zeitstempel',
      maxtime: 'Höchster Zeitstempel',
      sin: 'Sinus Funktion',
      cos: 'Kosinus Funktion',
      tan: 'Tangens Funktion',
      asin: 'Arcussinus Funktion',
      acos: 'Arcuskosinus Funktion',
      atan: 'Arcustangens Funktion',
      degress: 'Grad (Winkel)',
      randians: 'rad (Winkel)',
      sing: 'Symbolische Funktion',
      ceil: 'Aufrunden',
      floor: 'Abrunden',
      round: 'Runden',
      exp: 'Exponentialfunktion',
      ln: 'Natürlicher Logarithmus',
      log10: 'Logarithmus zur Basis 10',
      sqrt: 'Quadratwurzel',
      abs: 'Absolutwert',
      string_cont: 'Prüft ob eine Sequenz im String enthalten ist',
      string_mat: 'Prüft ob ein String einem regulären Ausdruck entspricht',
      tok: 'Gibt die k Datenpunkte mit dem höchten Wert in der Zeitreihe zurück',
      bottomk: 'Gibt die k Datenpunkte mit dem geringsten Wert in der Zeitreihe zurück',
      time_d: 'Differenz zwischen den Zeitpunkten zweier auffeinanderfolgenden Datenpunkten',
      diff: 'Differenz zwischen zwei auffeinanderfolgenden Datenpunkten',
      non: 'Absolutwert der Differenz zwischen zwei auffeinanderfolgenden Datenpunkten',
      deriv: 'Grad der Änderung zwischen einem Wert und dem vorhergehenden',
      non_n: 'Absolutwert der Änderungsrate zwischen aufeinanderfolgenden Punkten',
      now: 'Aktuelles Datum',
      save: 'Speichern',
      run: 'Ausführen',
      stop: 'Anhalten',
      sqlserchText: 'Das Query wurde erfolgreich beendet und keine Daten wurden zurückgegeben',
      sqlrun: 'Query wird aktuell ausgeführt',
      type: 'type',
      description: 'Beschreibung',
    },
    standTable: {
      export: 'Exportieren',
      running: 'Laufende Ergebnisse',
      download: 'Download',
      maxdownload: 'Download bis zu 100000 Datenpunkte',
      serchtime: 'Dauer des Queries',
      queryline: 'Anzahl der Ergebnisszeilen',
      function: 'Funktion',
      data: 'Daten',
      savequery: 'Query speichern',
      queryname: 'Name des Queries',
      deleteArry: 'Stapel löschen',
      importTip: 'Import erfolgreich',
    },
    controlPage: {
      dataList: 'Datenliste',
      address: 'Host Oder IP Adresse',
      storage: 'Anzahl Der Lagergruppen',
      entity: 'Anzahl Der Unternehmen',
      physics: 'Physik',
      total: 'Gesamtmenge',
      monitor: 'Überwachungsindikatoren',
      search: 'Abfragestatistik',
      allMode: 'Alle Modi',
      devMode: 'Entwicklermodus',
      opeMode: 'Modus Betreiber',
      chartBtn: 'Diagramm Bedienfeld',
      listBtn: 'Gruppe Liste',
      jvm: 'JVM Zeiger',
      cpu: 'CPU Zeiger',
      memory: 'Speicherindikator',
      store: 'Massenspeicherindikatoren',
      write: 'Schreib Zeiger',
      isearch: 'Abfragekriterien',

      iName: 'Zeigerbezeichnung',
      iType: 'Zeigertypen',
      zxjg: 'Zuletzt aufgetretene Ergebnisse',
      zxzb: 'Aktuelle Kennzahlergebnisse',
      sql: 'SQL Anweisungen',
      runTime: 'Laufzeit',
      exeTime: 'Ausführungszeit(s)',
      slowSearch: 'Langsames Abfragen',
      lastTime: 'Letzte Ausführung',
      runCount: 'Anzahl der Ausführungen',
      querySentence: 'Abfrageanweisungen',
      exeResult: 'Ausführungsergebnisse',
      downloadLog: 'Download Log',
      totalUseTime: 'Gesamte verstrichene Zeit (ms)',
      garmmarUseTime: 'Syntax zeitaufwändig (ms)',
      ybyUseTime: 'Kompilierungszeit (ms)',
      yhbyUseTime: 'Kompilierungszeit optimieren (ms)',
      exeUseTime: 'Ausführungszeit (ms)',

      JVMThread: 'JVM Zeiger–Threads',
      JVMRecycle: 'JVM Zeiger–Rückgewinnung',
      JVMMemory: 'JVM Zeiger–Speicher',
      JVMClasses: 'JVM Zeiger-Classes',

      selectMetrics: 'Wählen Sie Einen Zeigertyp Aus',

      GCEchart: 'GC Vorkommen und Zeitaufwand (in Minuten, fast 15 Minuten)',
      JVMClassEchart: 'Anzahl der class, die JVM entladen/ geladen',
      YGCEchart: 'Gründe und Zeitaufwand für YGC Ausfälle',
      FGCEchart: 'Gründe und Zeitaufwand für FGC Ausfälle',
      JAVATypeEchart: 'Anzahl der java Threads pro Typ',
      JAVATimeEchart: 'Threads pro Zeitraum in java',
      MemoryEchart: 'Speichernutzung',
      BufferEchart: 'Puffernutzungsgröße',
      CPUEchart: 'CPU-Zeit in Prozent',
      IOEchart: 'Festplatten-E/ A-Durchsatz',
      FileCountEchart: 'Anzahl der Dateien, Statistik',
      FileSizeEchart: 'Dateigröße Statistiken',
      WriteEchart: 'Statistiken und Zeitaufwand für erfolgreiche Schreibvorgänge',
      SearchEchart: 'Statistiken zu erfolgreichen Abfragen',
      ApiEchart: 'Schnittstelle zeitaufwändig',
      ApiQPSEchart: 'Schnittstelle QPS',

      fgcCount: 'fgc Anzahl',
      ygcCount: 'ygc Anzahl',
      fgcTime: 'fgc Zeit',
      ygcTime: 'ygc Zeit',
      Ptotal: 'gesamt',
      Pentries: 'Einträge',
      EachPage: 'Jede Seite',
    },
  },
};

export default de;
