import { inject } from '@angular/core';
import { CanActivateFn, ResolveFn, Router, Routes } from '@angular/router';
import { Login } from './login/login';
import { NaruciteljiComponent } from './narucitelj/narucitelj';
import { RadniNalogComponent } from './radni-nalog/radni-nalog';
import { AddNaruciteljComponent } from './add-narucitelj/add-narucitelj';
import { UpdateNaruciteljComponent } from './update-narucitelj/update-narucitelj';
import { AddRadniNalogComponent } from './add-radni-nalog/add-radni-nalog';
import { UpdateRadniNalogComponent } from './update-radni-nalog/update-radni-nalog';
import { SearchComponent } from './search/search';
import { PostavkeComponent } from './postavke';
import { AddAktivnostComponent } from './add-aktivnost/add-aktivnost';
import { UpdateAktivnostComponent } from './update-aktivnost/update-aktivnost';
import { NaruciteljDetailsComponent } from './narucitelj-details/narucitelj-details';
import { RadniNalogDetailsComponent } from './radni-nalog-details/radni-nalog-details';
import { UskoroIsticeComponent } from './uskoro-istice/uskoro-istice';
import { KorisnikLoginComponent } from './korisnik-login/korisnik-login';
import { KorisnikRegisterComponent } from './korisnik-register/korisnik-register';
import { KorisnikDashboardComponent } from './korisnik-dashboard/korisnik-dashboard';
import { KorisnikProfilComponent } from './korisnik-profil/korisnik-profil';
import { KorisnikPonudeComponent } from './korisnik-ponude/korisnik-ponude';
import { KorisnikObavijestiComponent } from './korisnik-obavijesti/korisnik-obavijesti';
import { KorisnikRecenzijeComponent } from './korisnik-recenzije/korisnik-recenzije';
import { UserManagementComponent } from './user-management/user-management';
import { StatisticsComponent } from './statistics/statistics';
import { SustavLogoviComponent } from './sustav-logovi/sustav-logovi';
import { PonudeManagementComponent } from './ponude-management/ponude-management';
import { RecenzijeManagementComponent } from './recenzije-management/recenzije-management';
import { SendObavijestComponent } from './send-obavijest/send-obavijest';
import { ReportsComponent } from './reports/reports';
import { MyTasksComponent } from './my-tasks/my-tasks';
import { AktivnostiService } from './services/aktivnosti.service';
import { RadniNalog, RadniNalogService, UskoroIsticeItem } from './services/radni-nalog.service';
import { Narucitelj, NaruciteljaService } from './services/narucitelj.service';
import { AuthService } from './services/auth.service';
import { KorisnikService } from './services/korisnik.service';
import { catchError, forkJoin, map, of } from 'rxjs';

const isBrowserRuntime = () => typeof window !== 'undefined';

const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (state.url.includes('embedded=1')) {
    return true;
  }

  if (!isBrowserRuntime()) {
    return true;
  }

  if (window.self !== window.top) {
    return true;
  }

  return authService.getToken() ? true : router.createUrlTree(['/login']);
};

const korisnikAuthGuard: CanActivateFn = (_route, _state) => {
  const korisnikService = inject(KorisnikService);
  const router = inject(Router);

  if (!isBrowserRuntime()) return true;
  return korisnikService.getToken() ? true : router.createUrlTree(['/korisnik/prijava']);
};

const indexRedirectGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!isBrowserRuntime()) {
    // Avoid generating a prerendered hard redirect (/login) for static hosting under a sub-path.
    return true;
  }

  return authService.getToken()
    ? router.createUrlTree(['/radniNalozi'])
    : router.createUrlTree(['/login']);
};

const radniNaloziResolver: ResolveFn<RadniNalog[]> = () =>
  !isBrowserRuntime()
    ? of([])
    :
  forkJoin({
    nalozi: inject(RadniNalogService).getAll(),
    aktivnosti: inject(AktivnostiService).getAll().pipe(catchError(() => of([])))
  }).pipe(
    map(({ nalozi, aktivnosti }) => {
      const nazivById = new Map<number, string>();
      (aktivnosti ?? []).forEach((item) => {
        if (item.id && item.naziv) {
          nazivById.set(item.id, item.naziv);
        }
      });

      return (nalozi ?? []).map((nalog) => ({
        ...nalog,
        aktivnosti: (nalog.aktivnosti ?? [])
          .map((aktivnost) => {
            if (typeof aktivnost === 'number') {
              return nazivById.get(aktivnost) ?? String(aktivnost);
            }

            const normalized = String(aktivnost ?? '').trim();
            const numericId = Number(normalized);
            if (Number.isFinite(numericId)) {
              return nazivById.get(numericId) ?? normalized;
            }

            return normalized;
          })
          .filter((value) => value.length > 0)
      }));
    }),
    catchError(() => of([]))
  );

const naruciteljiResolver: ResolveFn<Narucitelj[]> = () =>
  !isBrowserRuntime()
    ? of([])
    :
  inject(NaruciteljaService).getAll().pipe(catchError(() => of([])));

const uskoroIsticeResolver: ResolveFn<UskoroIsticeItem[]> = () =>
  !isBrowserRuntime()
    ? of([])
    :
  inject(RadniNalogService).getUskoroIstice().pipe(
    map((result) => (Array.isArray(result) ? result : (result ? [result] : []))),
    catchError(() => of([]))
  );

export const routes: Routes = [
  { path: '', component: Login, pathMatch: 'full', canActivate: [indexRedirectGuard] },  
  {
    path: 'narucitelji',
    component: NaruciteljiComponent,
    canActivate: [authGuard],
    resolve: { narucitelji: naruciteljiResolver },
    runGuardsAndResolvers: 'always'
  },
  { path: 'addNarucitelj', component: AddNaruciteljComponent, canActivate: [authGuard] },
  { path: 'updateNarucitelj/:id', component: UpdateNaruciteljComponent, canActivate: [authGuard] },
  {
    path: 'radniNalozi',
    component: RadniNalogComponent,
    canActivate: [authGuard],
    pathMatch: 'full',
    resolve: { radniNalozi: radniNaloziResolver },
    runGuardsAndResolvers: 'always'
  },
  { path: 'addRadniNalog', component: AddRadniNalogComponent, canActivate: [authGuard] },
  { path: 'updateRadniNalog/:id', component: UpdateRadniNalogComponent, canActivate: [authGuard] },
  { path: 'updateRadniNalog', component: UpdateRadniNalogComponent, canActivate: [authGuard] },
  { path: 'addAktivnost', component: AddAktivnostComponent, canActivate: [authGuard] },
  { path: 'updateAktivnost/:id', component: UpdateAktivnostComponent, canActivate: [authGuard] },
  { path: 'search', component: SearchComponent, canActivate: [authGuard] },
  {
    path: 'uskoroistice',
    component: UskoroIsticeComponent,
    canActivate: [authGuard],
    resolve: { uskoroIstice: uskoroIsticeResolver },
    runGuardsAndResolvers: 'always'
  },
  { path: 'postavke', component: PostavkeComponent, canActivate: [authGuard] },
  { path: 'narucitelj/:id', component: NaruciteljDetailsComponent, canActivate: [authGuard] },
  { path: 'radni-nalog/:id', component: RadniNalogDetailsComponent, canActivate: [authGuard] },
  { path: 'login', component: Login },
  // Admin / Manager / Worker routes
  { path: 'users', component: UserManagementComponent, canActivate: [authGuard] },
  { path: 'statistics', component: StatisticsComponent, canActivate: [authGuard] },
  { path: 'logovi', component: SustavLogoviComponent, canActivate: [authGuard] },
  { path: 'ponude-management', component: PonudeManagementComponent, canActivate: [authGuard] },
  { path: 'recenzije-management', component: RecenzijeManagementComponent, canActivate: [authGuard] },
  { path: 'send-obavijest', component: SendObavijestComponent, canActivate: [authGuard] },
  { path: 'reports', component: ReportsComponent, canActivate: [authGuard] },
  { path: 'my-tasks', component: MyTasksComponent, canActivate: [authGuard] },
  // Korisnik (customer) portal routes
  { path: 'korisnik/prijava', component: KorisnikLoginComponent },
  { path: 'korisnik/registracija', component: KorisnikRegisterComponent },
  { path: 'korisnik/dashboard', component: KorisnikDashboardComponent, canActivate: [korisnikAuthGuard] },
  { path: 'korisnik/profil', component: KorisnikProfilComponent, canActivate: [korisnikAuthGuard] },
  { path: 'korisnik/ponude', component: KorisnikPonudeComponent, canActivate: [korisnikAuthGuard] },
  { path: 'korisnik/obavijesti', component: KorisnikObavijestiComponent, canActivate: [korisnikAuthGuard] },
  { path: 'korisnik/recenzije', component: KorisnikRecenzijeComponent, canActivate: [korisnikAuthGuard] }
];
