import { Component, OnInit } from '@angular/core';

import { List } from 'src/app/models/list';
import { HomePageService } from 'src/app/services/home-page.service';
import { ListMutationService } from 'src/app/services/list-mutation.service';
import { FormGroup,  FormBuilder,  Validators } from '@angular/forms';

@Component({
  selector: 'app-home-page',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomePageComponent implements OnInit {

  lists:List[];

  createUserActive:boolean;

  id:String;
  email:String;
  firstName:String;
  lastName:String;
  role:String;
  password:String;

  fileToUpload: File = null;

  angForm: FormGroup;

  constructor(private homePageService:HomePageService,
              private listMutationService:ListMutationService,
              private fb: FormBuilder){
    this.createForm();
  }

  createForm() {
    this.angForm = this.fb.group({
      email: ['', Validators.required ],
      firstName: [''],
      lastName: [''],
      role: [''],
      password: ['']
    });
  }

  ngOnInit() {
     this.homePageService
         .getAllLists()
         .subscribe(lists => this.lists = lists);
    this.createUserActive = false;
  }

  activateCreateUser():void{
    this.createUserActive = true;
    this.id = '';
    this.email = '';
    this.firstName = '';
    this.lastName = '';
    this.role = '';
    this.password = ''
    this.fileToUpload = null;
  }

  deactivateCreateUser():void{
    this.createUserActive = false;
  }

  activateUpdateUser(list:List):void{
    this.id = list.id;
    this.createUserActive = true;
    this.email = list.email;
    this.firstName = list.firstName;
    this.lastName = list.lastName;
    this.role = list.role;
    this.password = '';
    this.fileToUpload = null;
  }

  saveUser():void{

    if (this.angForm.invalid){
      return;
    }

    if (this.id == '' || this.id == null) {
      this.listMutationService
        .createUser(this.email, this.firstName, this.lastName, this.role, this.password)
        .subscribe(newList => {

          if (this.fileToUpload != null) {
            this.listMutationService.postFile(newList.id, this.fileToUpload);
          }

          this.homePageService
            .getAllLists()
            .subscribe(lists => this.lists = lists);
        });

    } else {
      this.listMutationService
        .updateUser(this.id, this.email, this.firstName, this.lastName, this.role, this.password)
        .subscribe(newList => {
          this.homePageService
            .getAllLists()
            .subscribe(lists => this.lists = lists);
        });

      if (this.fileToUpload != null) {
        this.listMutationService.postFile(this.id, this.fileToUpload);
      }
    }

    this.createUserActive = false;
  }

  deleteUser(list:List):void{
    this.listMutationService
      .deleteUser(list.id)
      .subscribe(newList => {
        this.homePageService
          .getAllLists()
          .subscribe(lists => this.lists = lists);
      });
    this.createUserActive = false;
  }

  handleFileInput(files: FileList) {
    this.fileToUpload = files.item(0);
  }
}
