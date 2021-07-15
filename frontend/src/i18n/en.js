import enLocale from 'element-plus/lib/locale/lang/en';

const en = {
  [enLocale.name]: {
    el: enLocale.el,
    message: {
      hello: 'hello world',
    },
    about: {
      'line-2': 'About us',
      'line-3': 'Visual management tool of IotDB',
      'line-4': `IotDB Admin is a GUI interface of IotDB, providing all the Adding, Deleting, Altering and Querying operations. Besides that, Accessing Control is also built. It extremely simplifies the use of IotDB and has very little learning cost. `,
      'line-5': `IotDB is one of the best time series database in our opinion. We will always try our best to advance the development and application of time series database, making contribution to rise of native open source ability and ecosystem development.Welcome everyone of you to join us, waiting for you! Contact us:`,
      'line-6-text': 'Scan wechat',
      'line-7': 'Version: 0.12',
      'back-btn': 'Return to work page',
    },
    common: {
      submit: 'submit',
      cancel: 'cancel',
      detail: 'detail',
      delete: 'delete',
      edit: 'edit',
      operation: 'operation',
      save: 'save',
    },
    databasem: {
      newStoreGroup: 'new storage group',
      query: 'query',
      newDevice: 'new entity',
      newQuery: 'new query',
    },
    rootPage: {
      chinalang: 'chinese',
      englishlang: 'english',
      loginoutText: 'login out',
      about: 'about us',
      help: 'help book',
      databaseManagement: 'database management',
      dataList: 'data list',
      newdatasource: 'new data source',
      newQueryWindow: 'new query window',
      nodatasource: 'it is has no data source at present, please',
    },
    loginPage: {
      account: 'account',
      password: 'password',
      placeholderAccount: 'please input account',
      placeholderPassword: 'please input password',
      forgetPassWord: 'forget passWord',
      signIn: 'sign in',
      forgetPassword: 'forget password',
      forgetPasswordTip: 'please contact system administrator',
      accountEmptyTip: 'account can not be empty',
      accountContentTip: 'the user name must be made up of letters, numbers, underscores, and cannot start with numbers and underscores',
      accountLengthTip: 'the user name must be greater than or equal to 3 characters and less than or equal to 32 characters',
      passwordEmptyTip: 'password can not be empty',
      passwordLenghtTip: 'password must be greater than or equal to 6 digits. please check the number of digits',
      welcomeLogin: 'welcome log in to IotDB database management system',
      loginErrorTip: 'incorrect user name or password, please re-enter',
    },

    sourcePage: {
      alias: 'alias',
      host: 'host',
      port: 'port',
      username: 'username',
      password: 'password',
      addDialogTitle: 'new source',
      editDialogTitle: 'edit source',
      eg: 'example:127.0.0.1',
      aliasEmptyTip: 'alias can not be empty',
      hostEmptyTip: 'host can not be empty',
      hostErrorTip: 'host pattern is incorrect',
      portErrorTip: 'port pattern is incorrect',
      newUserEmptyTip: 'username can not be empty',
      newPasswordTip: 'password can not be empty',
      newUserErrorTip: 'please input right pattern',
      newUserErrorTip1: 'the length of user name must be contain with 4~255',
      newpasswordErrorTip1: 'the length of password must be contain with 4~255',
      aliasErrorTip: 'the length of alias must be contain with 3~100',
      portEmptyTip: 'port can not be empty',
      usernameEmptyTip: 'username can not be empty',
      passwordEmptyTip: 'password can not be empty',
      userAccount: 'user account',
      newAccount: 'new account',
      baseConfig: 'base config',
      accountPermit: 'account permit',
      userNameTitle: 'username:',
      passwordTitle: 'password:',
      groupInfo: 'group info',
      groupName: 'group name',
      description: 'description',
      line: 'line',
      path: 'path',
      range: 'range',
      func: 'function',
      selectAlias: 'alias',
      selectGroup: 'group',
      selectDevice: 'device',
      selectTime: 'time series',
      createGroup: 'SET_STORAGE_GROUP',
      createUser: 'CREATE_USER',
      deleteUser: 'DELETE_USER',
      editPassword: 'MODIFY_PASSWORD',
      listUser: 'LIST_USER',
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
      test: 'connection test',
      testBtnLabel: 'test',
      testResult: 'connection test passed',
      noAuthTip: 'you have no right to take this operation at present',
      addAuthBtn: 'add auth',
      modifySuccessLabel: 'you have modify password successful',
      addSuccessLabel: 'you have create user successful',
      deleteUserSuccessLabel: 'you have delete user successful',
      addFirstLabel: 'please do create user present first',
      deleteAuthLabel: 'you have delete authorition successful',
      operateAuthLabel: 'you have operate authorition successful',
      deleteGroupLabel: 'you have delete group successful',
      newGroupSuccessLabel: 'you have new or edit group successful',
      deleteAuthConfirm: 'are you sure to delete this auth?',
      deleteSourceConfirm: 'are you sure to delete this source?',
      newSourceSuccessLabel: 'you have new or edit source successful',
      addAuthFirstLabel: 'please do auth add present first',
      authTips: 'attention please: after check the [LIST_USER], you can make the [CREATE_USER]、[DELETE_USER]、[MODIFY_PASSWORD]、[GRANT_USER_PRIVILEGE]、[REVOKE_USER_PRIVILEGE] take effect',
    },
    storagePage: {
      alias: 'alias',
      creator: 'creator',
      createTime: 'createTime:',
      ttl: 'ttl:',
      description: 'description',
      deviceName: 'device name:',
      newDevice: 'new device',
      line: 'line',
      operation: 'operation',
      secondLabel: 'second',
      minuteLabel: 'minute',
      hourLabel: 'hour',
      dayLabel: 'day',
      weekLabel: 'week',
      monthLabel: 'month',
      yearLabel: 'year',
      groupName: 'group name:',
      groupDescription: 'group description:',
      tips: 'tips:it means that ttl will infinite if you do not fill',
      ttlErrTips: 'ttl and ttl unit must be exist at the same time',
      groupNamePlaceholder: 'please input group name',
      groupNameLengthTips: 'the length of group name must be contain with 0~255',
      descriptionLengthTips: 'the length of group name must be contain with 0~100',
      deleteGroupConfirm: 'are you sure to delete this group?',
      deleteDeviceConfirm: 'This operation will lead to the deletion of the device could not be resumed, confirmed to delete?',
    },
    device: {
      dataconnection: 'data connection',
      selectdataconnection: 'Please select data link',
      devicename: 'device name',
      description: 'device description',
      group: 'storage group',
      physical: 'physical quantity',
      addphysical: 'add physical quantity',
      delete: 'delete',
      creator: 'creator',
      createTime: 'createTime',
      newValue: 'new value',
      deletecontent1: 'Are you sure you want to delete',
      deletecontent2: 'After deletion, the  data will be lost and cannot be recovered',
      cencel: 'cencel',
      ok: 'ok',
      physicalname: 'physical name',
      datatype: 'data type',
      codingmode: 'coding mode',
      physicaldescr: 'description of physical quantity',
      action: 'operation',
      datatrends: 'data trends',
      datatrend: 'Trend of physical quantity data',
      time: 'date',
      look: 'look',
      inputdevice: 'Please enter entity name',
      inputdecr: 'Please enter entity description',
      pyname: 'Physical quantities must be composed of words, numbers and underscores and chinese',
      pynamel: 'The physical quantity must be composed of words, numbers and underscores. The physical quantity must be 0 characters and 255 characters',
      pynamecopy: 'Duplicate physical quantity name, please modify',
      deletetitle: 'Successfully deleted',
      canceldeletion: 'Cancel deletion',
      addpydataa: 'Add up to 2000 pieces of data',
      tips: 'Tips',
      pleaseinput: 'Please fill in the name of physical quantity',
      selectdata: 'Data type must be selected for physical quantity',
      savesuccess: 'Saved successfully',
      minphysical: 'The physical quantity cannot be 0',
      must: 'Please enter the required content',
      newquery: 'New Query',
      inputfunction: 'Please enter a function name',
      selectp: 'Please Select Group',
      deleteSuccess: 'Delete Success',
    },
    sqlserch: {
      Aggregate: 'Aggregate',
      math: 'Math',
      string: 'String',
      select: 'Select',
      sum: 'Trend calculation',
      date: 'Date',
      count: 'Count the number of time series',
      avg: 'Average value of time series data',
      sum1: 'Sum of time series data',
      fristvalue: 'First inserted data value',
      lastvalue: 'Latest inserted data value',
      minvalue: 'Minimum data value',
      maxvalue: 'Maximum data value',
      mintime: 'Minimum time stamp',
      maxtime: 'Maximum time stamp',
      sin: 'Sine function',
      cos: 'cosine function',
      tan: 'Tangent function',
      asin: 'Anti sine function',
      acos: 'Arccosine function',
      atan: 'Arctangent function',
      degress: 'Angle degree',
      randians: 'Rotation radian',
      sing: 'Symbolic function',
      ceil: 'Round up',
      floor: 'Round down',
      round: 'rounding',
      exp: 'E-based index',
      ln: 'Logarithm with base e',
      log10: 'Base 10 logarithm',
      sqrt: 'take a square root',
      abs: 'Find the absolute value',
      string_cont: 'Used to determine whether a string exists in a string',
      string_mat: 'Used to determine whether a string can be matched by a regular expression regex',
      tok: 'Returns the k data points with the largest median value in a time series',
      bottomk: 'Return the k data points with the minimum value in a time series',
      time_d: 'The difference between the time stamp of a data point and the time stamp of the previous data point in the statistical sequence',
      diff: 'Statistics the difference between the value of a data point and the value of the previous data point in the sequence',
      non: 'The absolute value of the difference between the value of a data point and the value of the previous data point in the statistical sequence',
      deriv: 'The rate of change of a data point in a statistical sequence relative to the previous data point',
      non_n: 'The absolute value of the change rate of a data point relative to the previous data point in a statistical sequence',
      now: 'now date',
      save: 'save',
      run: 'run',
      stop: 'stop',
    },
    standTable: {
      export: 'export',
      running: 'Running results',
      download: 'download',
      maxdownload: 'Download up to 100000 pieces of data',
      serchtime: 'Query time',
      queryline: 'Number of query lines',
      function: 'function',
      data: 'data',
      savequery: 'save query',
      queryname: 'query name',
    },
  },
};

export default en;
