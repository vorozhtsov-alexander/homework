import { Injectable }  from '@angular/core';
import {AuthenticationService} from "./authentication.service";

@Injectable({
  providedIn: 'root'
})
export class AppInitService {

  constructor(private authenticationService: AuthenticationService) {
  }

  Init() {

    return new Promise(resolve => {
      // attempt to refresh token on app start up to auto authenticate
      this.authenticationService.refreshToken()
        .subscribe()
        .add(resolve);
    });
  }
}
