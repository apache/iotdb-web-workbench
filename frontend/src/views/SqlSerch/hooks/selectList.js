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

const selectList = {
  SET: ['SET STORAGE GROUP TO <FullPath>', 'SET TTL TO <StorageGroupPath> <Integer>'],
  CREATE: [
    'CREATE TIMESERIES <FullPath> WITH <AttributeClauses>',
    'CREATE TIMESERIES <FullPath> WITH <AttributeClauses> TAGS (<Clauses>) ATTRIBUTES(<Clauses>)',
    'CREATE USER <userName> <password>',
    'CREATE ROLE <roleName>',
  ],
  ALTER: [
    'ALTER TIMESERIES <FullPath> ADD TAGS <Clauses>',
    'ALTER TIMESERIES <FullPath> ADD ATTRIBUTES <Clauses>',
    'ALTER TIMESERIES <FullPath> DROP <TagNames>',
    'ALTER TIMESERIES <FullPath> RENAME <OldName> TO <NewName>',
    'ALTER TIMESERIES <FullPath> SET <Clauses>',
    'ALTER TIMESERIES <FullPath>',
    'ALTER USER <username> SET PASSWORD <password>',
  ],
  DELETE: ['DELETE STORAGE GROUP <PrefixPath>', 'DELETE TIMESERIES <PrefixPath>', 'DELETE FROM <PrefixPath>', 'DELETE FROM <PrefixPath> WHERE <WhereClause>'],
  UNSET: ['UNSET TTL TO <StorageGroupPath>'],
  UPSERT: ['UPSERT ALIAS=<NewName> TAGS(<Clauses>) ATTRIBUTES(<Clauses>)'],
  SHOW: [
    'SHOW STORAGE GROUP',
    'SHOW STORAGE GROUP <PrefixPath>',
    'SHOW TIMESERIES',
    'SHOW TIMESERIES (<PrefixPath>)? Where <WhereClause>',
    'SHOW TIMESERIES (<PrefixPath>)? LIMIT <Integer> OFFSET <Integer>',
    'SHOW LATEST TIMESERIES (<PrefixPath>)?',
    'SHOW DEVICES <PrefixPath> WITH STORAGE GROUP',
    'SHOW ALL TTL',
    'SHOW TTL <StorageGroupNames>',
    'SHOW CHILD PATHS <prefixPath>',
    'SHOW CHILD NODES <prefixPath>',
    'SHOW DEVICES',
    'SHOW DEVICES <PrefixPath>',
    'SHOW DEVICES WITH STORAGE GROUP',
    'SHOW SHOW DEVICES <PrefixPath> WITH STORAGE GROUP',
  ],
  COUNT: ['COUNT TIMESERIES <Path> GROUP BY LEVEL=<Integer>', 'COUNT NODES <PrefixPath> LEVEL=<Integer>'],
  INSERT: ['INSERT INTO <PrefixPath>(TIMESTAMP, <PointNames>) VALUES(<Values>)'],
  SELECT: [
    'SELECT <SelectClause> FROM <FromClause>',
    'SELECT <SelectClause> FROM <FromClause> WHERE <WhereClause>',
    'SELECT <SelectClause> FROM <FromClause> WHERE <WhereClause> GROUP BY <GroupByTimeClause>',
    'SELECT <SelectClause> FROM <FromClause> WHERE <WhereClause> FILL <FillClause>',
  ],
  ORDER: ['ORDER BY TIME ASC | DESC'],
  LIMIT: ['LIMIT <Integer> [OFFSET <Integer>]?'],
  SLIMIT: ['SLIMIT <Integer> [OFFSET <Integer>]?'],
  DROP: ['DROP ROLE <roleName>'],
  GRANT: ['GRANT USER <userName> PRIVILEGES <privileges> ON 	<nodeName>', 'GRANT ROLE <roleName> PRIVILEGES <privileges> ON 	<nodeName>', 'GRANT <roleName> TO <userName>'],
  REVOKE: ['REVOKE USER <userName> PRIVILEGES <privileges> ON 	<nodeName>', 'REVOKE ROLE <roleName> PRIVILEGES <privileges> ON 	<nodeName>', 'REVOKE <roleName> FROM <userName>'],
  LIST: [
    'LIST USER',
    'LIST ROL',
    'LIST PRIVILEGES USER <username> ON 	<path>',
    'LIST PRIVILEGES ROLE <roleName> ON 	<path>',
    'LIST USER PRIVILEGES <username>',
    'LIST ROLE PRIVILEGES <roleName>',
    'LIST ALL ROLE OF USER <username>',
    'LIST ALL USER OF ROLE <roleName>',
  ],
};
export default selectList;
