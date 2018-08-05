import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {AuthenticatedLayoutComponent} from './layout/authenticated-layout.component';
import {LoginLayoutComponent} from './layout/login-layout.component';
import {HomeComponent} from './view/home/home.component';
import {LoginComponent} from './view/login/login.component';
import {LoginGuard} from './service/login.guard';
import {LoginService} from './service/login.service';
import {SidebarComponent} from "./component/sidebar/sidebar.component";
import {ScheduleComponent} from "./view/schedule/schedule.component";

@NgModule({
  declarations: [
    AppComponent,
    AuthenticatedLayoutComponent,
    LoginLayoutComponent,
    HomeComponent,
    LoginComponent,
    SidebarComponent,
    ScheduleComponent
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule],
  providers: [LoginService, LoginGuard],
  bootstrap: [AppComponent]
})
export class AppModule {
}
