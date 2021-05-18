import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {Apollo} from 'apollo-angular';
import gql from 'graphql-tag';

import { List } from 'src/app/models/list';

const homePageQuery = gql
  `{
  getAllUsers {
    id,
    email,
    firstName,
    lastName,
    role
  }
}`;

@Injectable({
  providedIn: 'root'
})
export class HomePageService {

  constructor(private apollo: Apollo) { }

  getAllLists():Observable<List[]> {
    return this.apollo
        .watchQuery({query: homePageQuery})
        .valueChanges
        .pipe(map(result => result['data']['getAllUsers']));
  }
}
