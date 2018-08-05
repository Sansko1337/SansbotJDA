import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthenticatedLayoutComponent} from './layout/authenticated-layout.component';
import {LoginLayoutComponent} from './layout/login-layout.component';
import {HomeComponent} from './view/home/home.component';
import {LoginComponent} from './view/login/login.component';
import {LoginGuard} from './service/login.guard';
import {ScheduleComponent} from "./view/schedule/schedule.component";

const routes: Routes = [
  {
    path: '',
    component: AuthenticatedLayoutComponent,
    canActivate: [LoginGuard],
    children: [
      {
        path: '',
        component: HomeComponent
      },{
        path: 'schedule',
        component: ScheduleComponent
      }
    ]
  },
  {
    path: '',
    component: LoginLayoutComponent,
    children: [
      {
        path: 'login',
        component: LoginComponent
      }
    ]
  },
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
