import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpErrorResponse, HttpEventType } from  '@angular/common/http';
import { Observable} from 'rxjs';
import { map} from 'rxjs/operators';

import {Apollo} from 'apollo-angular';
import gql from 'graphql-tag';

import { List } from 'src/app/models/List';

const createUserMutation = gql
`mutation createUser($email:String!, $firstName:String!, $lastName:String!, $role:String!, $password:String!){
    list:createUser(details:{email:$email, firstName:$firstName, lastName:$lastName, role:$role, password:$password}){
      id,
      email,
      firstName,
      lastName,
      role
    }
}`;

const updateUserMutation = gql
  `mutation updateUser($id:UUID, $email:String!, $firstName:String!, $lastName:String!, $role:String!, $password:String!){
    list:updateUser(details:{id:$id, email:$email, firstName:$firstName, lastName:$lastName, role:$role, password:$password}){
      id,
      email,
      firstName,
      lastName,
      role
    }
}`;

const deleteListMutation = gql
`mutation deleteUser($id:UUID){
  success:deleteUser(id:$id)
}`;

@Injectable({
  providedIn: 'root'
})
export class ListMutationService {

  constructor(private apollo: Apollo,
              private httpClient: HttpClient) { }

  createUser(email:String, firstName:String, lastName:String, role:String, password:String):Observable<List> {
    return this.apollo.mutate({
        mutation: createUserMutation,
        variables:{email:email, firstName:firstName, lastName:lastName, role:role, password:password}
      }).pipe(map(result => {
        return result['data']['list'];
      }));
  }

  updateUser(id:String, email:String, firstName:String, lastName:String, role:String, password:String):Observable<List> {
    return this.apollo.mutate({
      mutation: updateUserMutation,
      variables:{id:id, email:email, firstName:firstName, lastName:lastName, role:role, password:password}
    }).pipe(map(result => {
      return result['data']['list'];
    }));
  }

  deleteUser(id:String):Observable<boolean> {
    return this.apollo.mutate({
        mutation: deleteListMutation,
        variables:{id:id}
      }).pipe(map(result => result['success']));
  }

  postFile(id:String, fileToUpload: File) {
    const endpoint = 'http://localhost:8080/users/upload-avatar/'+id; //todo move server url in settings
    const formData: FormData = new FormData();
    formData.append('file', fileToUpload, fileToUpload.name);
    this.httpClient
      .post(endpoint, formData)
      .subscribe();
  }
}
