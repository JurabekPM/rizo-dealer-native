# Kod xaritasi — 2026-10-22

Barcha quyidagi `.kt` yo‘llar `app/src/main/java/com/example/dealerappnative/`ga nisbatan. Birinchi jadvaldan faqat kerakli oqimni o‘qing. Keyin `rg` bilan chaqiruvchilarni toping. To‘liq inventar pastda.

## Vazifadan kodga

| Vazifa / oqim | Avval o‘qiladigan fayllar |
|---|---|
| Ishga tushirish va rollar | `MainActivity.kt`; `DealerEntry.kt`; `DealerApp.kt`; `screens/AgentScreen.kt`; `navigation/DealerDestination.kt`; `navigation/AgentDestination.kt` |
| Admin visit/list/details | `screens/AdminVisitsFlowScreen.kt`; `screens/VisitsScreen.kt`; `screens/VisitDetailScreen.kt` |
| Agent visit lifecycle | `screens/AgentVisitsScreen.kt`; `screens/AgentVisitDetailScreen.kt`; `model/AgentVisitSession.kt`; `data/AgentVisitSessions.kt`; `data/CompletedAgentVisitSamples.kt` |
| Visit card/results/shared animation | `ui/components/VisitStoreCard.kt`; `ui/components/VisitResultsRow.kt`; `ui/components/VisitResultsTransition.kt`; `ui/components/VisitListViewport.kt` |
| Admin menu va revisions | `screens/MenuScreen.kt`; `screens/AdminRevisionsScreen.kt`; `screens/MonthlyOperationsScreen.kt`; `data/MonthlyHistoryDocuments.kt` |
| Admin qaytarish arxivi | `screens/AdminReturnsScreen.kt`; `screens/AdminReturnDetailScreen.kt`; `screens/AdminReturnCreateScreen.kt`; `data/AdminReturnDocuments.kt` |
| Ikkala rol return formasi | `screens/VisitReturnScreen.kt`; `screens/ReturnProductPickerScreen.kt`; `ui/components/ReturnProductComponents.kt`; `ui/components/ReturnProductInfoSheet.kt`; `data/ReturnCatalog.kt` |
| Return sabablar/persistence | `ui/components/ReturnReasonSheet.kt`; `screens/ReturnReasonSettingsScreen.kt`; `model/ReturnReason.kt`; `data/ReturnReasonStore.kt`; `data/VisitReturnStore.kt`; `model/VisitReturnRecord.kt` |
| Agent history/filter/details | `screens/AgentHistoryScreen.kt`; `screens/AgentHistorySectionScreen.kt`; `screens/AgentHistoryArchiveScreen.kt`; `screens/AgentHistoryVisitDetailScreen.kt`; `screens/AgentHistoryOperationScreen.kt`; `model/AgentHistory.kt`; `ui/components/AgentHistoryFilterSheet.kt` |
| Monthly history/sales hisoblari | `data/AgentHistoryReports.kt`; `data/MonthlyHistoryDocuments.kt`; `data/HistoryReturnTotals.kt`; `data/AgentHistorySales.kt`; `screens/AgentSalesHistoryScreen.kt`; `ui/components/HistoryMonthCard.kt` |
| Zayavki/Dostavki | `screens/RequestsScreen.kt`; `screens/DeliveriesScreen.kt`; `ui/components/OrderListScreen.kt`; `ui/components/OrderCard.kt`; `model/OrderUiModel.kt` |
| Inkasatsiya | `screens/CollectionsScreen.kt`; `model/DebtDirection.kt` |
| Magazinlar list/details | `screens/StoresScreen.kt`; `screens/StoreDetailScreen.kt`; `screens/AgentStoresScreen.kt`; `screens/AgentStoreDetailScreen.kt`; `model/StoreUiModel.kt`; `data/StoreSampleData.kt` |
| Statistika va sana filtri | `screens/AgentStatisticsScreen.kt`; `ui/components/StatisticsPeriodFilter.kt`; `ui/components/StatisticsDatePicker.kt`; `model/StatisticsPeriod.kt`; `data/AgentStatisticsSampleData.kt` |
| Qarzlar/collapsing search/shared store | `screens/AgentStoreDebtsScreen.kt`; `ui/components/AgentStoreDebtHeader.kt`; `ui/components/AgentStoreDebtCard.kt`; `ui/components/AgentStoreSharedTransition.kt`; `data/AgentStoreDebtData.kt`; `model/AgentStoreDebt.kt` |
| Xarita entry/edit/persistence | `ui/components/StoreActionsHost.kt`; `screens/StoreMapScreen.kt`; `data/StoreLocationPreferences.kt`; `data/VisitMapStore.kt`; `data/DealerMapDefaults.kt` |
| Xarita render/sheet/marker | `ui/components/StoreMapView.kt`; `ui/components/DraggableMapSheet.kt`; `ui/components/StoreMapMarker.kt`; `ui/components/StoreMapInfoSheet.kt` |
| GPS/geocoder/route | `location/DeviceLocationServices.kt`; `location/StoreAddressFormatter.kt`; `location/StoreRouteService.kt`; `model/StoreLocation.kt`; `model/StoreRoute.kt`; `model/StoreRouteSummary.kt` |
| Dizayn/tab/buttonlar | `ui/theme/DealerDesign.kt`; `ui/theme/AgentVisitDesign.kt`; `ui/theme/AgentDesign.kt`; `ui/theme/DealerPrototypeTheme.kt`; `ui/components/SoftCardPress.kt`; `ui/components/SoftActionButtons.kt`; `ui/components/ActiveHistoryTabs.kt` |

## Ma’lumotlarni saqlash

| SharedPreferences | Mas’ul fayl | Saqlanadigan holat |
|---|---|---|
| `agent_visit_sessions` | `data/AgentVisitSessions.kt` | real start/finish, natijalar, izoh, foto URI, finish sababi, demo flag |
| `visit_returns` | `data/VisitReturnStore.kt` | visit ID → bitta return record; items, vaqt, sabab snapshoti |
| `return_reason_settings` | `data/ReturnReasonStore.kt` | system/custom sabablar, enabled holati |
| `prototype_store_locations` | `data/StoreLocationPreferences.kt` | store ID → lat/lon/address + km/vaqt summary |

## Mavjud unit test sinflari

App testlari `app/src/test/java/com/example/dealerappnative/` ichida. Source o‘zgartirishdan oldin tegishli testni o‘qing; baseline 57 ta.

- `app/src/test/java/com/example/dealerappnative/AdminReturnDocumentsTest.kt` — 3 test: archiveMonthsContainEachDocumentExactlyOnce, savedDocumentReplacesDemoAndUsesItsOwnDateReasonAndProducts, administratorArchiveIncludesSavedAgentReturns
- `app/src/test/java/com/example/dealerappnative/AgentHistoryFilterTest.kt` — 4 test: activeSessionsNeverAppearEvenWithoutFilters, filtersAreCombinedAndUseSessionResults, inclusiveDatesUseTashkentCalendarDay, fewerOperationsDoNotMakeCompletedVisitPartial
- `app/src/test/java/com/example/dealerappnative/AgentHistoryReportsTest.kt` — 4 test: onlyRecordedWorkCreatesReportsAndReopeningKeepsSameData, eachVisitHasItsOwnDocuments, planTotalsMatchSameMonthSameStoreSameOperation, dateGroupingUsesTashkentAndRetainsOlderRecordedMonths
- `app/src/test/java/com/example/dealerappnative/AgentHistorySalesTest.kt` — 3 test: salesSubtractReturnsUsingTheSameVisitDocuments, salesNeverIncludeAnActiveVisitOrAnotherMonth, totalsAndDrilldownStayWithTheirOwnStore
- `app/src/test/java/com/example/dealerappnative/AgentVisitSessionTest.kt` — 8 test: durationStartsAtActualTapAndContinuesAcrossMidnight, notesAndPhotosDoNotCountAsBusinessWork, otherRequiresNonBlankComment, completedWorkCanFinishWithoutReasonAndTimerStops, closingRetainsEvidenceAndDoesNotInventResults, clockGoingBackDoesNotProduceNegativeDuration, resultCardIsCompletedBeforeOpeningAndCannotFinishAgain, completedSamplePreservesExistingTimesCommentAndPhotos
- `app/src/test/java/com/example/dealerappnative/data/ReturnCatalogTest.kt` — 3 test: restoredOldDraftKeepsSelectionAndAddsUnselectedProducts, draftCannotExceedStockOrContainNegativeQuantities, sourceDocumentsAndProductIdentitiesRemainDistinct
- `app/src/test/java/com/example/dealerappnative/ExampleUnitTest.kt` — 1 test: addition_isCorrect
- `app/src/test/java/com/example/dealerappnative/MonthlyHistoryDocumentsTest.kt` — 3 test: monthlyRevisionCountEqualsItsActualDocuments, storeFilterAndCollectionHistoryUseRecordedWorkOnly, administratorRevisionArchiveUsesAdministratorStoresAndDates
- `app/src/test/java/com/example/dealerappnative/ReturnReasonTest.kt` — 4 test: systemPresetsHaveStableUniqueIdsAndDoNotDuplicateOther, disablingAReasonPreventsItsUseEvenWithAComment, standardReasonAllowsAnAdditionalNoteAndOtherRequiresOne, administratorNamesAreValidatedWithoutLosingDisabledReasons
- `app/src/test/java/com/example/dealerappnative/RevisionMonthBalancesTest.kt` — 3 test: onlyLatestRevisionBalancePerStoreIsCounted, currenciesAndFractionalBalancesStaySeparate, otherMonthsUnfinishedAndNonRevisionVisitsDoNotAffectBalance
- `app/src/test/java/com/example/dealerappnative/StatisticsPeriodTest.kt` — 6 test: weekStartsOnMondayAndEndsOnSunday, monthHandlesLeapYear, tashkentDayDoesNotShiftToPreviousUtcDay, customIntervalIncludesBothBoundaryDays, futureDaysOnlyAddPlansToTheCurrentWeek, oldRangeHasZeroActualsAndNoInvalidProgress
- `app/src/test/java/com/example/dealerappnative/VisitMapStoreTest.kt` — 3 test: agentAndHistoryMapsKeepTheSelectedVisitIdentity, administratorVisitMapsKeepTheirOwnStore, visitOnlySamplesDoNotAllInheritThePosLitePoint
- `app/src/test/java/com/example/dealerappnative/VisitReturnsTest.kt` — 3 test: returnDocumentUsesChosenProductsAndExactTotal, monthlyReturnTotalUsesDocumentMonthAndExcludesActiveVisits, monthlySalesSubtractTheSameCustomReturnAndDoNotMoveOtherWork
- `app/src/test/java/com/example/dealerappnative/VisitSampleConsistencyTest.kt` — 3 test: adminStatusesMatchResultBadges, agentHasMoreCardsMixedAtTheTopAndAllResultCounts, everyAgentCardResolvesToItsOwnDetails

## To‘liq Kotlin inventari

Fayl nomi mavjudlikni bildiradi; har bir tugma ishlashiga kafolat emas. Vizual-only holatlar uchun asosiy handoff hujjatiga qarang.

- `data/AdditionalVisitSampleData.kt`
- `data/AdminReturnDocuments.kt`
- `data/AgentDebtSampleStores.kt`
- `data/AgentHistoryReports.kt`
- `data/AgentHistorySales.kt`
- `data/AgentHomeSampleData.kt`
- `data/AgentMenuSampleData.kt`
- `data/AgentStatisticsSampleData.kt`
- `data/AgentStoreDebtData.kt`
- `data/AgentVisitSampleData.kt`
- `data/AgentVisitSessions.kt`
- `data/CompletedAgentVisitSamples.kt`
- `data/DealerMapDefaults.kt`
- `data/HistoryReturnTotals.kt`
- `data/MixedAgentVisitSampleData.kt`
- `data/MonthlyHistoryDocuments.kt`
- `data/PendingAgentVisitSampleData.kt`
- `data/ReturnCatalog.kt`
- `data/ReturnReasonStore.kt`
- `data/RevisionMonthBalances.kt`
- `data/StoreLocationPreferences.kt`
- `data/StoreSampleData.kt`
- `data/VisitMapStore.kt`
- `data/VisitReturnStore.kt`
- `data/VisitSampleData.kt`
- `DealerApp.kt`
- `DealerEntry.kt`
- `location/DeviceLocationServices.kt`
- `location/StoreAddressFormatter.kt`
- `location/StoreRouteService.kt`
- `MainActivity.kt`
- `model/AgentHistory.kt`
- `model/AgentHomeMetric.kt`
- `model/AgentStoreDebt.kt`
- `model/AgentVisitSession.kt`
- `model/AppRole.kt`
- `model/DebtDirection.kt`
- `model/OrderUiModel.kt`
- `model/ReturnReason.kt`
- `model/StatisticsPeriod.kt`
- `model/StoreLocation.kt`
- `model/StoreRoute.kt`
- `model/StoreRouteSummary.kt`
- `model/StoreUiModel.kt`
- `model/VisitResult.kt`
- `model/VisitReturnRecord.kt`
- `model/VisitStore.kt`
- `navigation/AgentBottomBar.kt`
- `navigation/AgentDestination.kt`
- `navigation/DealerBottomNavigation.kt`
- `navigation/DealerDestination.kt`
- `screens/AdminReturnCreateScreen.kt`
- `screens/AdminReturnDetailScreen.kt`
- `screens/AdminReturnsScreen.kt`
- `screens/AdminRevisionsScreen.kt`
- `screens/AdminVisitsFlowScreen.kt`
- `screens/AgentHistoryArchiveScreen.kt`
- `screens/AgentHistoryOperationScreen.kt`
- `screens/AgentHistoryScreen.kt`
- `screens/AgentHistorySectionScreen.kt`
- `screens/AgentHistoryVisitDetailScreen.kt`
- `screens/AgentMenuScreen.kt`
- `screens/AgentSalesHistoryScreen.kt`
- `screens/AgentScreen.kt`
- `screens/AgentStatisticsScreen.kt`
- `screens/AgentStoreDebtsScreen.kt`
- `screens/AgentStoreDetailScreen.kt`
- `screens/AgentStoresScreen.kt`
- `screens/AgentVisitDetailScreen.kt`
- `screens/AgentVisitsScreen.kt`
- `screens/CollectionsScreen.kt`
- `screens/DeliveriesScreen.kt`
- `screens/MenuScreen.kt`
- `screens/MonthlyOperationsScreen.kt`
- `screens/RequestsScreen.kt`
- `screens/ReturnProductPickerScreen.kt`
- `screens/ReturnReasonSettingsScreen.kt`
- `screens/RoleSelectionScreen.kt`
- `screens/RoutesScreen.kt`
- `screens/StoreDetailScreen.kt`
- `screens/StoreMapScreen.kt`
- `screens/StoresScreen.kt`
- `screens/VisitDetailScreen.kt`
- `screens/VisitReturnScreen.kt`
- `screens/VisitsScreen.kt`
- `ui/components/ActiveHistoryTabs.kt`
- `ui/components/AgentHistoryCard.kt`
- `ui/components/AgentHistoryDatePicker.kt`
- `ui/components/AgentHistoryDetails.kt`
- `ui/components/AgentHistoryFilterSheet.kt`
- `ui/components/AgentMenuRow.kt`
- `ui/components/AgentMetricCard.kt`
- `ui/components/AgentProgress.kt`
- `ui/components/AgentStoreDebtCard.kt`
- `ui/components/AgentStoreDebtHeader.kt`
- `ui/components/AgentStoreSharedTransition.kt`
- `ui/components/AgentVisitControls.kt`
- `ui/components/ChildScreenHeader.kt`
- `ui/components/DealerIcon.kt`
- `ui/components/DraggableMapSheet.kt`
- `ui/components/HistoryMonthCard.kt`
- `ui/components/MenuRow.kt`
- `ui/components/OrderCard.kt`
- `ui/components/OrderListScreen.kt`
- `ui/components/PrototypeCreateButton.kt`
- `ui/components/ReturnProductComponents.kt`
- `ui/components/ReturnProductInfoSheet.kt`
- `ui/components/ReturnReasonSheet.kt`
- `ui/components/ScreenHeader.kt`
- `ui/components/SearchField.kt`
- `ui/components/ShortcutCard.kt`
- `ui/components/SoftActionButtons.kt`
- `ui/components/SoftCardPress.kt`
- `ui/components/StatisticsDatePicker.kt`
- `ui/components/StatisticsPeriodFilter.kt`
- `ui/components/StoreActionsHost.kt`
- `ui/components/StoreMapInfoSheet.kt`
- `ui/components/StoreMapMarker.kt`
- `ui/components/StoreMapView.kt`
- `ui/components/VisitFinishSheet.kt`
- `ui/components/VisitListViewport.kt`
- `ui/components/VisitResultsRow.kt`
- `ui/components/VisitResultsTransition.kt`
- `ui/components/VisitStoreCard.kt`
- `ui/theme/AgentDesign.kt`
- `ui/theme/AgentVisitDesign.kt`
- `ui/theme/DealerDesign.kt`
- `ui/theme/DealerPrototypeTheme.kt`

## Koddan tashqari muhim joylar

- `app/src/main/res/drawable/`: original vektorlar va mavjud rasmlar.
- `app/src/main/res/font/`: Onest vaznlari.
- `app/src/main/assets/map/`: MapLibre JS/CSS, Bright style, HTML bridge va litsenziyalar.
- `app/src/main/assets/visit_samples/`: demo tashrif rasmlari.
- `figma-reference/`: asl eksportlar, o‘lchovlar va litsenziyalar; eski funksional izohlar joriy emasligi mumkin.
- `app/src/main/AndroidManifest.xml`: INTERNET va foreground location permissions; ACTION_DIALga CALL_PHONE kerak emas.
- `gradle/libs.versions.toml`, `app/build.gradle.kts`: haqiqiy build versiyalari.
- `docs/HANDOFF_BASELINE_SHA256.json`: source holati; `scripts/Compare-HandoffBaseline.ps1` o‘qish uchun solishtiruvchi script.

## Agent Заявка qo‘shimchasi — 2026-10-24

- `screens/AgentVisitRequestScreen.kt`: vizitdan kirish, faol-vizit cheklovi, form, tasdiqlash.
- `screens/RequestProductPickerScreen.kt`: tranzaksion qidiruv/tanlash.
- `ui/components/RequestProductComponents.kt`: original uslubdagi thumbnail/card, quantity sheet.
- `screens/VisitRequestDetailScreen.kt`: saqlangan zayavka va faol vizitda tahrirlash.
- `data/RequestCatalog.kt`, `data/VisitRequestStore.kt`, `model/VisitRequestRecord.kt`: order katalogi, lokal JSON, snapshot va validatsiya.
- `data/AgentVisitSessions.kt`: saveRequest; `model/AgentHistory.kt`, `data/AgentHistoryReports.kt`, `data/AgentHistorySales.kt`: haqiqiy request record bilan integratsiya.
- `VisitRequestsTest.kt`: total/validation/history/month boundary regressiyalari.

Mahsulot tanlashning Figma qismi: `ui/components/RequestPickerComponents.kt`, `RequestQuantitySheet.kt`, `RequestProductThumbnail.kt`, `ui/theme/RequestPickerDesign.kt`. `RequestCatalogCompatibilityTest.kt` UI saralashidan keyin eski draft ID/miqdorlari joyi o‘zgarmasligini tekshiradi.

Umumiy picker UI: model/PickerProduct.kt — readonly ko‘rinish contracti; ui/components/ProductPickerLayout.kt — header/footer. RequestPickerComponents/Counter/Thumbnail/QuantitySheet ikkala tanlash oynasiga xizmat qiladi, lekin Request va Return draft/stock/saving qoidalari alohida. ReturnCatalog.ktdagi stepReturnPickerQuantity/returnPickerTotal va ReturnCatalogTest stock/draft regressiyalarini tekshiradi.

## Rejadan tashqari Agent vizit — 2026-10-26

- ui/components/AgentUnplannedSheet.kt: uch amal tanlovi.
- screens/AgentUnplannedVisitsScreen.kt: searchable physical store picker; active store hint.
- screens/AgentVisitsScreen.kt: list/draft/detail, UUID va saqlangan unplanned vizitlar.
- model/UnplannedVisit.kt, data/AgentUnplannedVisitStore.kt: physical-store snapshot va alohida lokal yozuv.
- screens/AgentVisitCollectionScreen.kt: active vizitdagi UZS amount; AgentVisitSession.recordCollection/AgentVisitSessions.saveCollection.
- VisitStore.sourceStore + VisitMapStore.storeForVisit: maps/phone physical ID; session/shared animation visit ID.
- UnplannedVisitTest.kt: identity isolation, active/start rules, notes/results preservation, actual collection history and repeated-store monthly sales.

## Vizit scroll regressiyasi

- ui/components/VisitResultsTransition.kt: doimiy shared/lookahead host; uni conditional olib tashlamang yoki listni oddiy rootga ko‘chirmang.
- app/src/androidTest/java/com/example/dealerappnative/VisitScrollRegressionTest.kt: synthetic80-card list;3×detail/work/Back, ikki tomon scroll, tab detach/reattach, viewport equality. Faqat o‘z in-memory ma’lumoti. Mavjud Espresso eski telefon Androidida ishlamagani uchun public platform sync va state scroll consumeri orqali ishlaydi.

## Faol vizit kamera/timer — 2026-10-27

- ui/components/VisitPhotoAdder.kt: camera-only direct capture, ActivityResult va pending URI; AgentVisitControls.kt faqat mavjud tile/notes/preview/timingni joylashtiradi.
- data/VisitCameraPhotos.kt + res/xml/visit_photo_paths.xml + AndroidManifest provider: tor doiradagi durable capture fayllari, empty/cancel cleanup.
- model/VisitDuration.kt / VisitDurationTest.kt: HH:mm:ss boundary, midnight/24h, actual timestamp/frozen finish.
- androidTest/VisitCameraPhotosTest.kt: o‘z generated test JPEG, URI/grants/provider/thumbnail/cancel; real kamera yoki user galereyasi ochilmaydi.

## Parallel vizitlar

- model/AgentVisitSession.kt: canStartAgentVisit va agentVisitStatus; model/VisitStore.kt UI label/color (NotVisited compatibility identity).
- data/AgentVisitSessions.kt: activeVisitIds/activeStoreIds, per-store independent start/finish, same-store duplicate guard.
- screens/AgentUnplannedVisitsScreen.kt: Set<String> physical active stores; singular business activeStoreIddan foydalanmang.
- VisitStatusTest/UnplannedVisitTest va androidTest/AgentParallelVisitsTest: lifecycle labels, new parallel rule, actual isolated persistence.


## Admin diler tahriri — 2026-10-28

MenuScreen top pencil → DealerApp → screens/DealerProfileScreen.kt. model/DealerProfile.kt; data/DealerProfilePreferences.kt (administrator_dealer_profile/profile); DealerProfileImages.kt; ui/components/DealerProfileFields.kt, DealerProfileMedia.kt, DealerProfileWorkingHours.kt; screens/DealerProfileLocationScreen.kt. DealerProfileTest + androidTest/DealerProfilePersistenceTest. Separate local profile draft; no Agent/visit/store accounting changes. Handoff §15.
