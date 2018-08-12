import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Track} from "../../model/track";
import {Endpoints} from "../../core/endpoints";
import {Observable} from "rxjs/index";

@Injectable()
export class ScheduleService {

  private http: HttpClient;

  constructor(http: HttpClient) {
    this.http = http;
  }

  public getUpcommingTracks(): Observable<Track[]> {
    return this.http.get<Track[]>(Endpoints.SCHEDULE);
  }

  public getCurrentTrack(): Observable<Track> {
    return this.http.get<Track>(Endpoints.CURRENT_TRACK);
  }
}
